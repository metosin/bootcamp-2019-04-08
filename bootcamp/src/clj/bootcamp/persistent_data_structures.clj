(ns bootcamp.persistent-data-structures
  (:require [clojure.test :refer :all]
            [clojure.string :as string]))

;
; Peristent data-structures:
;

(assoc {:foo "foo"} :bar "bar")                             ;=> {:foo "foo", :bar "bar"}
(dissoc {:foo "foo" :bar "bar"} :foo)                       ;=> {:bar "bar"}

(deftest persistent-data-structures-tests
  (let [book {:title   "The Joy of Clojure"
              :langs   #{:clojure}
              :authors [:fogus]}]

    ; Associate ('add') map with new element
    (is (= {:title   "The Joy of Clojure"
            :langs   #{:clojure}
            :authors [:fogus]
            :pages   328} ; This is the 'added' key/val
           (assoc book :pages 328)))

    ; The 'book' is not changed (it's immutable):
    (is (= {:title   "The Joy of Clojure"
            :langs   #{:clojure}
            :authors [:fogus]}
           book))

    ; Dis-associate by key
    (is (= {:title   "The Joy of Clojure"
            :langs   #{:clojure}}
           (dissoc book :authors)))))

; Great support for nested collections:

(deftest add-author-with-update-in-tests
  (let [book {:title   "The Joy of Clojure"
              :langs   #{:clojure}
              :authors [:fogus]}]

    ; Excercise:
    ; ----------
    ; Your task: add keyword :houser to the authors vector:

    (is (= {:title   "The Joy of Clojure"
            :langs   #{:clojure}
            :authors [:fogus :houser]}
           ;                 ^^^^^^^-------< here

           (update-in book [:authors] conj :houser)))))

(deftest add-100-points-to-game-score-tests
  (let [game {:player "John McCarthy"
              :state {:game-started 1429970768115
                      :score {:level 6
                              :points 1237}}}]

    ; Excercise:
    ; ----------
    ; Your task: add 100 points to 'points'

    (is (= {:player "John McCarthy"
            :state {:game-started 1429970768115
                    :score {:level 6
                            :points 1337}}}
           ;                        ^^^^---------< here's the +100

           (update-in game )))))

