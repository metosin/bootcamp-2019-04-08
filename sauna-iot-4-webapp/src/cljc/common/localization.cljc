(ns common.localization)

(def defaults
  {:page-title "Sauna-IoT"
   :loading "We are loading stuff, please wait."
   :follow "Follow"
   :stop-following "Stop following"
   :new-todo "What do you need to do?"
   :we-love-clojure "We <3 Clojure"})

(defn tr [k]
  (or (get defaults k)
      (str "Not yet localized: << " k " >>")))
