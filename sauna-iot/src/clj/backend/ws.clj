(ns backend.ws
  (:require [backend.measurements :as measurements]
            [clojure.tools.logging :as log]
            [eines.core :as eines]
            [eines.middleware.rsvp :as rsvp]
            [eines.server.immutant :as eines-immutant]
            [mount.core :refer [defstate]]
            [clj-time.coerce :as t-coerce]
            ))

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

;;; XXX(soija) Kill these

(defn create-uuid! []
  (str (java.util.UUID/randomUUID)))

(defonce todos (atom [{:id (create-uuid!)
                       :text "Eat and drink"}
                      {:id (create-uuid!)
                       :text "Go to sauna!"}]))

(defn add-todo! [message]
  (let [{:keys [send! send-fn]} message]
    (swap! todos conj {:id (create-uuid!)
                       :text (:body message)})
    (broadcast! {:message-type :todos
                 :body @todos})))

(defmethod handle-message :get-todos
  [{:keys [send!]}]
  (send! {:message-type :todos
          :body @todos}))

(defmethod handle-message :new-todo
  [message]

  (todo/add-todo! message))
