(ns backend.db
  (:require [backend.config :as config]
            [backend.db.support]
            [conman.core :as conman]
            [luminus-migrations.core :as migration]
            [mount.core :refer [defstate]]))

(def migration-base-opts
  {:migration-dir "migrations/"
   :migration-table-name "schema_migrations"})

(defn create-migration!
  "Create migration script templates

  Creates new templates for up and down migration script in the fold
  `resources/migrations/`."
  [name]
  (migration/create name migration-base-opts))

(defn run-migrations!
  "Run the outstanding migrations"
  [con]
  (migration/migrate ["migrate"] (assoc migration-base-opts :db con)))

(defn config->jdbc-url
  [{:keys [db-host db-port db-database db-user db-password]}]
  (format "jdbc:postgresql://%s:%s/%s?user=%s&password=%s"
          db-host db-port db-database db-user db-password))

;; NB: Dynamic so that it can be locally rebound to a transaction.
(defstate ^:dynamic *db*
  :start (doto (conman/connect! {:jdbc-url (config->jdbc-url config/config)})
           (run-migrations!))
  :stop (conman/disconnect! *db*))
