(ns user
  (:require [clojure.tools.namespace.repl :as tn-repl]
            [mount.core :as mount]))

(defn go
  []
  ;; Having `require` here postpones the loading of `backend.system` until the
  ;; first invocation of `(go)`.
  (require 'backend.system)
  (mount/start))

;; This restarts the system WITHOUT reloading the namespace that have changed
;; since last reset.
(defn reset
  []
  (mount/stop)
  (go))

;; This reloads the altered namespaces and then restarts the system.
#_
(defn reset
  []
  (mount/stop)
  (tn-repl/refresh :after 'user/go))
