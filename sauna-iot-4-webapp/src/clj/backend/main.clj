(ns backend.main
  (:gen-class))

(defn -main [& args]
  (require 'backend.system)
  (backend.system/start))
