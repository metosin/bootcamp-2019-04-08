(ns frontend.main
  (:require [cljs-time.coerce :as t-coerce]
            [cljs-time.core :as t]
            [cljs-time.format :as t-fmt]
            [common.localization :refer [tr]]
            [eines.client :as eines]
            [re-frame.core :as rf]
            [re-frame.db :as rf-db]
            [reagent-dev-tools.core :as dev-tools]
            [reagent-dev-tools.state-tree :as dev-state]
            [reagent.core :as reagent]))

;;
;; State
;;

(def initial-state
  {:not-followed #{}
   :measurements ::undefined})

(rf/reg-event-db :initialize (constantly initial-state))

(defn get-sensors
  [{:keys [measurements not-followed]}]
  (if (not= measurements ::undefined)
    (letfn [(keep-newer [sensors {:keys [device_id] :as measurement}]
              (update sensors
                      device_id
                      #(max-key :measured_on measurement (or % measurement))))
            (amend-activity [{:keys [device_id] :as measurement}]
              (assoc measurement :active? (not (not-followed device_id))))]
      (->> measurements
        (reduce keep-newer {})
        (map (comp amend-activity val))
        (sort-by :device_id)
        (sort-by (comp not :active?))))
    ::undefined))

(rf/reg-event-db
  :add-measurements
  (fn [db [_ measurements reset?]]
    (let [sorted-measurements
          (->> measurements
            (sort-by :measured_on)
            (map #(update % :measured_on t-coerce/from-date)))]
      (if reset?
        (assoc db :measurements (vec sorted-measurements))
        (update db :measurements into sorted-measurements)))))

(rf/reg-sub
  :sensors
  (fn [db _]
    (get-sensors db)))

(defn get-measurements
  [{:keys [measurements not-followed]} result-count]
  (if (not= measurements ::undefined)
    (->> measurements
      (filter (complement not-followed))
      (sort-by (comp - t-coerce/to-long :measured_on))
      (take result-count))
    ::undefined))

(rf/reg-sub
  :measurements
  (fn [db _]
    (get-measurements db 5)))

(dev-state/register-state-atom "App state" rf-db/app-db)

;;
;; Views
;;

(defn sensor-item-view
  [sensor]
  (let [{:keys [device_id active? temperature]} sensor]
    [:ul
     [:li device_id]
     [:li (if active?
            (tr :stop-following)
            (tr :follow))]
     [:li (if active?
            (str temperature " °C")
            "-")]]))

(defn sensor-list-view
  []
  (let [sensors @(rf/subscribe [:sensors])]
    (if (= sensors ::undefined)
      [:div.todo-list
       [:p "loading sensors"]]
      [:div.todo-list
       (for [s sensors]
         [sensor-item-view s])])))

(defn measurement-item-view
  [{:keys [device_id measured_on temperature] :as measurement}]
  [:ul
   [:li device_id]
   [:li (->> measured_on (t-fmt/unparse (t-fmt/formatters :hour-minute-second)))]
   [:li (str temperature " °C")]])

(defn measurement-list-view
  []
  (let [measurements @(rf/subscribe [:measurements])]
    (if (= measurements ::undefined)
      [:div.todo-list
       [:p "loading measurements"]]
      [:div.todo-list
       (for [m measurements]
         [measurement-item-view m])])))

(defn main-view []
  [:div.todo-container
   [:h1.todo-title (tr :page-title)]
   [:div.todo-content
    [sensor-list-view]
    [measurement-list-view]]
   [:div.todo-footer (tr :we-love-clojure)]])

;;
;; Websockets
;;

(defn on-connect []
  (js/console.log "Connected to backend")
  (eines/send! {:message-type :get-history}
               (fn [response]
                 (js/console.log "Received measurement history payload")
                 (js/console.log "Payloyd " (pr-str (:body response)))
                 (when-let [measurements (:body response)]
                   (rf/dispatch [:add-measurements measurements true])))))

(defn on-message [{:keys [message-type body] :as message}]
  (case message-type
    :measurement-history (rf/dispatch [:add-measurements body true])
    :new-measurement (rf/dispatch [:add-measurements [body] false])
    (js/console.warn "Got unrecognized message from backend: " (pr-str message))))

(defn on-close []
  (js/console.log "Disconnected from backend."))

(defn on-error []
  (js/console.warn "Disconnected from backend because of an error."))

;;
;; Main
;;

(defn ^:export main []
  (eines/init! {:on-connect on-connect
                :on-message on-message
                :on-close on-close
                :on-error on-error})
  (rf/dispatch-sync [:initialize])
  (reagent/render [main-view] (.getElementById js/document "app"))
  (if-let [dev-el (.getElementById js/document "dev")]
    (reagent/render [dev-tools/dev-tool {}] dev-el)))

(main)
