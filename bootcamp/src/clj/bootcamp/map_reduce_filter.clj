(ns bootcamp.map-reduce-filter
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [bootcamp.data.books :as b]))

;;;
;;; Map, reduce and filter:
;;;
;;; Common functional programming involves:
;;;
;;;   filter  - When you want to limit the elements from seq
;;;   map     - When you want to transform each element in some way
;;;   reduce  - When you want to reduce a seq to a value
;;;

;;
;; Filter (and remove):
;; --------------------
;;

(filter odd? [1 2 3 4])                                     ;=> (1 3)
(remove odd? [1 2 3 4])                                     ;=> (2 4)

;; Exercise:
;; ---------
;;
;; Take a look at the books in bootcamp.data.books namespace.

(type b/books)                                              ;=> clojure.lang.PersistentVector
(vector? b/books)                                           ;=> true
(count b/books)                                             ;=> 6

;; Find out how many books are about Clojure?

;;
;; Map:
;; ----
;;

;; Take a function and apply it to each value in a seq

(inc 41)                                                    ;=> 42
(inc 1336)                                                  ;=> 1337

(map inc [41 1336])                                         ;=> (42 1337)

;; map can take more than one seq too

(map + [41 1237] [1 100])                                   ;=> (42 1337)

;; The above is:
;;   take 41 and 1, apply them to +
;;   take 1237 and 100, apply them to +
;; In other words:
;;   (cons (+ 41 1) (cons (+ 1237 100)))

(subs "foobar" 3)                                           ;=> "bar"

(map subs
     ["foobar" "hello, world" "programming"]
     [3 7 8])                                               ;=> ("bar" "world" "ing")

;; Exercise:
;; ----------
;;
;; Continue with the books about Clojure. This time, produce a seq
;; of page counts from all the books that are about Clojure:




;;
;; Reduce:
;; -------
;;

(reduce + 10 [1 2 3 4])                                     ;=> 20
;; (+ 10 1)   => 11
;; (+ 11 2)   => 13
;; (+ 13 3)   => 16
;; (+ 16 4)   => 20

(reduce + [1 2 3 4])                                        ;=> 10
;; (+ 1 2)    => 3
;; (+ 3 3)    => 6
;; (+ 6 4)    => 10

(reduce (fn [acc value]
          (str acc ", " value))
        ["java" "python" "clojure"])                        ;=> "java, python, clojure"

;; While reduce is very useful when computing with data structures it cannot
;; be overstated how important the following idiom is:

#_
(reduce transform-fn initial-state operations)

;; Here:
;;
;; - initial-state is the initial state of a system
;; - operations is a sequence of commands that operate on the state
;; - transform-fn is computes a new state from the previous state by applying
;;   the next operation to it

;; Exercise:
;; ----------
;;
;; Continue with the previous example where we got the seq of number of pages.
;; Now answer to questions: how many pages we have about Clojure in total?
