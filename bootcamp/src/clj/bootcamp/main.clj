(ns bootcamp.main
  (:gen-class))

(defn -main [& args]
  (require 'bootcamp.compojure)
  (let [start-server (resolve 'bootcamp.compojure/start-server)]
    (start-server))
  (println "Server ready"))
