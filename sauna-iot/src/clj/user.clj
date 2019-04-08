(ns user
  (:require [mount.core :as mount]))

(defn go
  []
  (require 'backend.system)
  (mount/start))

(defn reset
  []
  (require 'backend.system)
  (mount/stop)
  ;; Add ns reloading with tools.namespace
  (mount/start))
