(ns bootcamp.compojure
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer [ok] :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [bootcamp.jetty-server :as jetty]
            [schema.core :as s]))

;;
;; REST API for storing user messages.
;;

; Schema for message:
(s/defschema Message
  {:sender s/Str
   :message s/Str})

; Storage for our messages:
(def db (atom []))

; Ring handler:
(def api-handler
  (api
    {:swagger
     {:ui "/api-docs"
      :spec "/swagger.json"
      :data {:info {:title "Bootcamp Compojure-api"
                    :description "Compojure Api example"}}}}

    (undocumented
      (GET "/" []
        (resp/temporary-redirect "/index.html")))

    (context "/api" []

      (GET "/" []
        :return [Message]
        (ok @db))

      (GET "/:sender" []
        :path-params [sender :- s/Str]
        :return [Message]
        (ok (filter (fn [m] (= (:sender m) sender)) @db)))

      (POST "/" []
        :body [message Message]
        :return [Message]
        (ok (swap! db conj message))))))

(def handler
  (-> #'api-handler
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))

(defn start-server []
  (jetty/start-server #'handler))

(comment
  (start-server))
