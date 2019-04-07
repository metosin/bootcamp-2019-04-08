(ns bootcamp.hello-cljs-world
  (:require [dommy.core :as dommy :refer-macros [sel sel1]]))

(js/console.log "Hello, ClojureScript world!")

(defonce click-counter (atom 0))

(defn click! [e]
  (js/console.log "click!" e)
  (.preventDefault e)
  (dommy/set-text! (sel1 :#click-count) (str (swap! click-counter inc))))

(dommy/listen! (sel1 :#click-button) :click click!)
