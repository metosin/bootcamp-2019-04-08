(ns backend.system
  (:require [mount.core :as mount]
            [backend.server]))

(defn start []
  (-> (mount/start) prn))

(defn stop []
  (-> (mount/stop) prn))
