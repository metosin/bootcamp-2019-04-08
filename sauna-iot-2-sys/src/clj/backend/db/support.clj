(ns backend.db.support
  (:require [cheshire.core :as json]
            [clj-time.coerce :as t-coerce]
            [clojure.java.jdbc :as jdbc])
  (:import (clojure.lang IPersistentMap
                         IPersistentVector)
           (java.sql Date
                     PreparedStatement
                     Timestamp)
           (org.joda.time DateTime)
           (org.postgresql.util PGobject)))

(defn ->pg-json
  [value]
  (doto (PGobject.)
    (.setType "json")
    (.setValue (json/generate-string value))))

(extend-protocol jdbc/ISQLValue
  IPersistentMap
  (sql-value [value]
    (->pg-json value))
  IPersistentVector
  (sql-value [value]
    (->pg-json value))
  org.joda.time.DateTime
  (sql-value [value]
    (t-coerce/to-sql-time value))
  java.util.Date
  (sql-value [value]
    (-> value .getTime Timestamp.)))

(defn <-pg-json
  [pg-obj]
  (json/parse-string (.getValue pg-obj) true))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [date _ _]
    (t-coerce/from-sql-date date))
  java.sql.Timestamp
  (result-set-read-column [timestamp _ _]
    (t-coerce/from-sql-time timestamp))
  PGobject
  (result-set-read-column [pg-obj _ _]
    (case (.getType pg-obj)
      "json" (<-pg-json pg-obj)
      "jsonb" (<-pg-json pg-obj)
      pg-obj)))
