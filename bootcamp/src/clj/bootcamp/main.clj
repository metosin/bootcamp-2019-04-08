(ns bootcamp.main
  (:gen-class))

(defn -main [& args]
  (case (first args)
    "server" (do
               (require 'bootcamp.compojure)
               (let [start-server (resolve 'bootcamp.compojure/start-server)]
                 (start-server))
               (println "Server ready"))
    (do
      (require 'bootcamp.hello-world)
      (let [hello (resolve 'bootcamp.hello-world/hello)]
        (hello "world")))))
