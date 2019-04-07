(ns setup.server
  (:require [clojure.java.io :as io]
            [org.httpkit.server :as httpkit]
            [ring.util.http-response :refer [ok]]
            [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
  (GET "/" [] (-> "public/index.html" io/resource io/input-stream ok))
  (route/resources "/")
  (route/not-found "Not found"))

(defn run [& args]
  (httpkit/run-server app {:port 8080 :join? false})
  "App running...")


(let [a [1 2]
      b [3 4]]
  (map + a b))