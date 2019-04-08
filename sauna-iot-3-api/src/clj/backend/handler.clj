(ns backend.handler
  (:require [backend.api :as api]
            [mount.core :refer [defstate]]))

(defstate ring-handler
  ;; NB: Var so that we can easily redefine the handler
  :start #'api/api-handler)
