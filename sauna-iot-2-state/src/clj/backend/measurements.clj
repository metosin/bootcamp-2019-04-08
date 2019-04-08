(ns backend.measurements
  (:require [backend.measurements.sql :as sql]
            [mount.core :refer [defstate]]))

;; NB: This allows someone down the line to watch changes
(defstate latest-measurement
  :start (atom nil))

(def get-devices sql/get-devices)

(defn get-device
  [device-id]
  (sql/get-device {:device_id device-id}))

(defn has-device?
  [device-id]
  (-> {:device_id device-id} sql/has-device? :exists))

(defn get-measurements
  ([]
   (get-measurements nil))
  ([device-ids]
   (->> (if-let [device-ids (-> device-ids dedupe not-empty)]
          (sql/get-measurements-for-devices {:device_ids device-ids})
          (sql/get-measurements))
     (map (fn [{:keys [device_id measured_on payload]}]
            (assoc payload
                   :device_id device_id
                   :measured_on measured_on))))))

(defn add-measurement
  [{:keys [device_id measured_on] :as measurement}]
  (sql/add-measurement! {:device_id device_id
                         :measured_on measured_on
                         :payload (dissoc measurement
                                          :device_id
                                          :measured_on)})
  (reset! latest-measurement measurement))
