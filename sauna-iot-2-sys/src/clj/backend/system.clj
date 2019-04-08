(ns backend.system
  (:require [backend.measurements]
            [mount.core :as mount]))

(defn start []
  (-> (mount/start) prn))

(defn stop []
  (-> (mount/stop) prn))
