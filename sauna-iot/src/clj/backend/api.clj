 (ns backend.api
  (:require [backend.measurements :as measurements]
            [clojure.tools.logging :as log]
            [compojure.api.sweet :as cmpj-sweet :refer [context GET POST]]
            [ring.util.http-response :refer [bad-request! not-found ok]]
            [schema.core :as s]))

;;;
;;; API schema
;;;

(def SuccessResponse
  {:success s/Bool
   (s/optional-key :error) s/Str})

(def Measurement
  {:device_id s/Str
   :measured_on org.joda.time.DateTime
   :temperature s/Num})

(def Device
  {:device_id s/Str})

(def MeasurementsResponse
  (merge SuccessResponse
         {(s/optional-key :data) [Measurement]}))

(def DevicesResponse
  (merge SuccessResponse
         {(s/optional-key :data) [Device]}))

(def DeviceInfoResponse
  (merge SuccessResponse
         {(s/optional-key :data) {:device_id s/Str
                                  :measurement_count s/Int}}))

;;;
;;; Middleware
;;;

(defn wrap-check-device-existence
  [handler]
  (fn [request]
    (log/info {::wrap-check-device-existence
               (select-keys request [:params :route-params])})
    (let [device-id (-> request :route-params :device-id)]
      (if (measurements/has-device? device-id)
        (handler request)
        (not-found {:success false
                    :error "no such device"})))))

;;;
;;; Handler
;;;

(defn success [data]
  (ok {:success true, :data data}))

(def api-handler
  ;; NB: The objects passing through the API boundary could be almost
  ;; automatically validated and coerced using plumatic.schema.
  (cmpj-sweet/api
      {:swagger
       {:ui "/api"
        :spec "/api/swagger.json"
        :data {:info {:version "1.0.0"
                      :title "Sauna IoT API"}
               :tags [{:name "devices"
                       :description "Sensor devices"}
                      {:name "measurements"
                       :description "Measurements from sensor devices"}]}}}

    (context "/api" []

      (context "/devices" []
        :tags ["devices"]

        (GET "/" []
          :summary "Get summary information for all available devices"
          :return DevicesResponse
          (log/info {:loc ::get-devices})
          (success (measurements/get-devices)))

        (context "/:device-id" []
          :path-params [device-id :- s/Str]
          :middleware [wrap-check-device-existence]

          (GET "/" []
            :summary "Get the detailed device information"
            :return DevicesResponse
            (log/info {:loc ::get-device-information, :device-id device-id})
            (-> device-id measurements/get-device vector success))

          (GET "/measurements" []
            :summary "Get device measurements"
            :tags ["devices" "measurements"]
            :return MeasurementsResponse
            (log/info {:loc ::get-device-measurements, :device-id device-id})
            (-> [device-id] measurements/get-measurements success))))

      (context "/measurements" []
        :tags ["measurements"]

        (GET "/" []
          :summary "Get all measurements"
          :return MeasurementsResponse
          (log/info {:loc ::get-measurements})
          (success (measurements/get-measurements)))

        (POST "/" []
          :summary "Add a new measurement"
          :body [measurement Measurement]
          :return SuccessResponse
          (log/info {:loc ::post-measurements, :measurement measurement})
          (measurements/add-measurement measurement)
          (ok {:success true}))))))

;;; ## Tasks
;;;
;;; - How would you add criterions for measurements queries?
;;;
;;; - How would you add unique identifier for measurements?
;;;
;;; - How would you make it possible to remove a measurement?  Update it?
;;;
;;; - How would you authenticate requests?
