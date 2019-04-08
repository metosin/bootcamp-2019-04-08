(ns backend.config
  (:require [cprop.core :as cprop]
            [mount.core :refer [defstate]]))

(defstate config
  :start (cprop/load-config))
