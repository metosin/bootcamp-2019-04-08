(ns backend.server
  (:require [backend.config :as config]
            [backend.handler :as handler]
            [luminus.http-server :as http]
            [mount.core :refer [defstate]]))

(defstate server
  :start (let [{:keys [server-host
                       server-port
                       server-path]} config/config]
           (http/start {:handler handler/ring-handler
                        :host server-host
                        :port server-port
                        :path server-path}))
  :stop (http/stop server))
