(ns backend.system
  (:require [backend.server]
            [mount.core :as mount]))

(defn start []
  (-> (mount/start) prn))

(defn stop []
  (-> (mount/stop) prn))
