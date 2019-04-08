(ns backend.handler
  (:require [backend.api :as api]
            [backend.web :as web]
            [backend.ws :as ws]
            [mount.core :refer [defstate]]))

(defstate ring-handler
  :start (some-fn ws/ws-http-handler
                  ;; NB: Var so that we can easily redefine the handler
                  #'api/api-handler
                  (web/create-static-handler)
                  (web/create-index-handler)))
