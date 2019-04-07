(ns bootcamp.ring
  (:require [ring.util.http-response :as r]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [muuntaja.middleware :refer [wrap-format]]
            [bootcamp.jetty-server :as jetty]))

(defn handler [request]
  (r/ok "Hello, world!!"))

; wrap-defaults: https://github.com/ring-clojure/ring-defaults
; muuntaja: https://github.com/metosin/muuntaja

(comment
  (jetty/start-server #'handler))

(comment
  (jetty/stop-server))
