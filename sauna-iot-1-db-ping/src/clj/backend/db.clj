(ns backend.db
  (:require [backend.db.support]
            [clojure.java.jdbc :as jdbc]
            [conman.core :as conman]
            [cprop.core :as cprop]))

;; NB: Dynamic so that it can be both bound late and rebound locally to a
;; transactions.

(defonce ^:dynamic *db* nil)

(defn config->jdbc-url
  [{:keys [db-host db-port db-database db-user db-password]}]
  (format "jdbc:postgresql://%s:%s/%s?user=%s&password=%s"
          db-host db-port db-database db-user db-password))

;; NB:  'foo   is short for  (quote foo)
;;      #'foo  is short for  (var foo)

(defn connect! []
  (let [config (cprop/load-config)
        db (conman/connect! {:jdbc-url (config->jdbc-url config)})]
    (alter-var-root #'*db* (constantly db))))

(defn disconnect! []
  (when *db*
    (conman/disconnect! *db*))
  (alter-var-root #'*db* (constantly nil)))

(defn current-db-time []
  (jdbc/query *db* "SELECT now() AS current_time;"))
