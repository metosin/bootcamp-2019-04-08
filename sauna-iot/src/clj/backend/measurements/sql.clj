(ns backend.measurements.sql
  (:require [backend.db :as db]
            [conman.core :as conman]))

(conman/bind-connection db/*db* "sql/measurements.sql")
