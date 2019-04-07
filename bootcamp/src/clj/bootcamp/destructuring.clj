(ns bootcamp.destructuring
  (:require [clojure.test :refer :all]
            [bootcamp.data.books :as b]))

;
; In bindings like:
;   (let [ HERE ]
;     )
; and
;   (defn foo [ HERE ]
;     )
; where you would normally have a symbol, you can place a
; data structure.
;

;
; Vector destructuring:
;

(let [v [1 2 3]
      a (nth v 0)
      b (nth v 1)
      c (nth v 2)]
  (+ a b c))                                                ;=> 6

; Now replace symbol v with destructuring:

(let [[a b c] [1 2 3]]
  ;   \-----/
  ;      |
  ;      \
  ;       -------- This is where 'v' used to be
  ;
  (+ a b c))

; Extra elements are ignored

(let [[a b c] [1 2 3 4 5 6 7]]
  (+ a b c))                                                ;=> 6

; Missing elements are set to nil

(let [[a b c] [1 2]]
  [a b c])                                                  ;=> [1 2 nil]

; Var-args work too:

(let [[a b & more] [1 2 3 4 5 6 7]]
  [a b more])                                               ;=> [1 2 (3 4 5 6 7)]

;
; Map destructuring:
;

(let [v     (first b/books)
      title (:title v)
      langs (:langs v)
      pages (:pages v)]
  [title langs pages])
;=> ["The Joy of Clojure" #{:clojure} 328]

(let [{title :title langs :langs pages :pages} (first b/books)]
  [title langs pages])
;=> ["The Joy of Clojure" #{:clojure} 328]

; Since it's very common to use same symbol name as the key, there's
; a shortkut:

(let [{:keys [title langs pages]} (first b/books)]
  [title langs pages])

; You can get the whole map too:

(let [{:keys [title langs pages] :as book} (first b/books)]
  [title langs pages (count (:authors book))])
;=> ["The Joy of Clojure" #{:clojure} 328 2]

; And have defaults:

(let [{:keys [a b c] :or {c 7}}   {:a 8 :b 27}]
  (+ a b c))
;=> 42

; And you can nest:

(let [{[_ author2] :authors} (first b/books)]
  author2)
;=> :houser

;
; Exercise:
; ----------
;

; Here's a predicate function topic? that accepts a language and a book, and
; returns truthy if the book is about the given language.
; Refactor this so that it uses destructuring.

(defn topic? [lang book]
  ;                ^^^^----------< detsructure here
  (get (:langs book) lang))
;      ^^^^^^^^^^^^^-------------< effect here

(deftest topic?-tests
  (is (topic? :clojure (first b/books)))
  (is (not (topic? :cobol (first b/books)))))

;
; Convert this to use destructuring too:
;

(defn price [order]
  (let [item-price (or (get order :item-price) 0)
        item-count (or (get order :item-count) 1)]
    (* item-price item-count)))

(deftest price-tests
  (is (= 42 (price {:item-price 21 :item-count 2})))
  (is (= 21 (price {:item-price 21})))
  (is (= 0 (price {:item-count 2}))))

;
; You know the drill:
;

(defn score [game]
  (let [level   (get game :level)
        ships   (get-in game [:hits :ships])
        aliens  (get-in game [:hits :aliens])
        rockets (get-in game [:hits :rockets])]
    (* level (+ (* 100 (or ships 0))
                (* 10  (or aliens 0))
                (* 200 (or rockets 0))))))

(deftest score-tests
  (is (= 45720 (score {:level 6
                       :hits  {:ships  32
                               :aliens 442}}))))
