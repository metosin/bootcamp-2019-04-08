(ns bootcamp.functional-programming
  (:require [clojure.test :refer :all]))

;;;
;;; Functional programming:
;;;

;; Some definitions:
;;
;;    ...style of building the structure and elements of computer programs,
;;    that treats computation as the evaluation of mathematical functions and
;;    avoids state and mutable data.
;;
;;    source: https://en.wikipedia.org/wiki/Functional_programming
;;
;; Commonly:
;;
;; * First-class and higher-order functions
;; * Pure functions
;;
;; Also commonly required:
;; * closures
;;
;; Modern functional concept:
;; * persistent data-structures

;;;
;;; First-class functions:
;;;

;; - functions are treated as "first-class citizens" in the language
;; - have literal expressions (a.k.a. anonymous functions)
;; - can be passed to other functions as arguments
;; - can be returned from other functions
;; - can be stored in data structures

(fn [a b] (+ a b))                      ;=> #function[...]
#(+ %1 %2)                              ;=> #function[...]

(defn apply-function [f v]
  (f v))

(apply-function (fn [x] (+ x x)) 1)     ;=> 2
(apply-function #(+ % %) 1)             ;=> 2

(defn return-function []
  (fn [x] (+ x x)))

((return-function) 1)                   ;=> 2

(def handler {:ping (fn [_]
                      {:type :pong})
              :add (fn [message]
                     {:type :sum
                      :result (+ (:lhs message)
                                 (:rhs message))})})

(defn handle [message]
  ((get handler (:type message)) message))

(handle {:type :ping})                  ;=> {:type :pong}
(handle {:type :add
         :lhs 20
         :rhs 22})                      ;=> {:type :sum, :result 42}

(defn process [handler message]
  ((get handler (:type message)) message))

(process handler {:type :ping})         ;=> {:type :pong}

;;;
;;; Higher-order functions:
;;;

;; Higher order functions are functions that either accept functions as
;; arguments or return functions or both.

(def numbers [1 2 3 4 5])

(apply + numbers)                       ;=> 15
(filter odd? numbers)                   ;=> (1 3 5)
(group-by #(mod % 3) numbers)           ;=> {1 [1 4], 2 [2 5], 0 [3]}
(partition-by #(quot % 2) numbers)      ;=> ((1) (2 3) (4 5))
(sort-by - numbers)                     ;=> (5 4 3 2 1)

;; Composition

(let [add-2 (partial + 2)]
  (add-2 40))                           ;=> 42

(let [sum (partial apply +)]
  (sum numbers))                        ;=> 15

(let [my-even? (comp not odd?)]
  (filter my-even? numbers))            ;=> (2 4)

;; Higher-order functions are key to high degree of composability.

;;;
;;; Purity:
;;;

;;
;; Clojure is a 'practical' language, no strict purity enforcement (like in
;; Haskell) How ever, clojure has a strong tendency towards purity (persistent
;; datastructures, STM)
;;

;; Example of a pure function:

(defn hello [message your-name]
  (str message ", " your-name))

;; Some non-pure versions:

(defn hello [message your-name]
  (println "generating hello message for" your-name)
  (str message ", " your-name))

(def message (atom nil))                ; known as "evil global mutable state"
                                        ; http://programmers.stackexchange.com/questions/148108/why-is-global-state-so-evil

(defn hello [your-name]
  (str (deref message) ", " your-name))

(reset! message "Hello")
(hello "world")                         ;=> "Hello, world"

(reset! message "Moi")
(hello "maailma")                       ;=> "Moi, maailma"

;; Why purity matters?

;; Pure functions are easy to develop, test and reason about.

;; Application with _only_ pure functions does nothing, so some non-pure parts
;; are always required. The Right Way(tm) is to be as pure as possible and
;; isolate non-pure functions to some non-pure units.

;;;
;;; Closures:
;;;

(defn greeter [message]            ; <- message here
  (fn [your-name]
    (str message ", " message)))   ; <- used as closure here

(let [g (greeter "Hullo")]
  ;; now g 'closes over string "Hullo"'
  (str (g "world!")))
