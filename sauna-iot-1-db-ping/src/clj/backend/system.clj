(ns backend.system
  (:require [backend.db]
            [mount.core :as mount]))

(defn start []
  (-> (mount/start) prn))

(defn stop []
  (-> (mount/stop) prn))
