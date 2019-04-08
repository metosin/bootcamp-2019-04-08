(ns backend.ws
  (:require [backend.measurements :as measurements]
            [clj-time.coerce :as t-coerce]
            [clojure.tools.logging :as log]
            [eines.core :as eines]
            [eines.middleware.rsvp :as rsvp]
            [eines.server.immutant :as eines-immutant]
            [mount.core :refer [defstate]]))

;;;
;;; Websocket handler
;;;

(defmulti handle-message :message-type :default ::not-found)

(defmethod handle-message ::not-found
  [{:keys [message-type]}]
  (log/warn {:loc ::handle-message
             :err "no route"
             :message-type message-type})
  (throw (ex-info (str "no route for " message-type) {})))

(def ws-http-handler
  (-> handle-message
    (eines/handler-context {:middlewares [(rsvp/rsvp-middleware)]})
    (eines-immutant/create-handler)))

(defn broadcast! [message]
  (let [sockets (vals @eines/sockets)]
    (doseq [socket sockets]
      (let [{:keys [send!]} socket]
        (send! message)))))

;;;
;;; Serving history and new measurements over websocket
;;;

(defn coerce-measured-on
  [measurement]
  (update measurement :measured_on t-coerce/to-date))

(defmethod handle-message :get-history
  [{:keys [send!] :as message}]
  (log/info {:loc ::handle-message-get-history
             :data message})
  (send! {:message-type :measurement-history
          :body (->> (measurements/get-measurements)
                  (map coerce-measured-on))}))

(defn on-new-measurement
  [measurement]
  (broadcast! {:message-type :new-measurement
               :body (coerce-measured-on measurement)}))

(defstate measurement-watcher
  :start (add-watch measurements/latest-measurement
                    ::measurement-watcher
                    #(on-new-measurement %4))
  :stop (remove-watch measurements/latest-measurement
                      ::measurement-watcher))
