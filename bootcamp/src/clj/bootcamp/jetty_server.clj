(ns bootcamp.jetty-server
  (:require [ring.adapter.jetty :as jetty]))

; Jetty server
(defonce server (atom nil))

; Stop Jetty
(defn stop-server []
  (when-let [s (deref server)]
    (println "Stopping Jetty...")
    (.stop s)
    (reset! server nil)))

; Start Jetty
(defn start-server [handler]
  (stop-server)
  (reset! server (jetty/run-jetty handler {:port 8080 :join? false}))
  (println "Jetty running..."))
