(ns bootcamp.sequences
  (:require [clojure.test :refer :all]))

;;;
;;; Sequences
;;;

;; mental map:
;;   Clojure: | Java:
;;   -------- |----------------
;;   list     | java.util.List
;;   seq      | java.util.Iterator

;; seq is a thing (cursor object?) that supports simple API:
;;  - first: give me current value
;;  - next:  give me a seq of next values
;; See clojure.lang.ISeq: https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/ISeq.java
;;
;; Also known as "cons cell": https://en.wikipedia.org/wiki/Cons
;; (although historical "cons cell" didn't require the second element to be a "cons cell")

;; Making a seq (or cons cell) from value:
;; (cons this-is-the-current-value the-sequence-of-the-rest)

(deftest make-seq-from-value
  (let [s (cons 42 nil)]
    (is (= 42  (first s)))
    (is (= nil (next s))))

  (let [s (cons 42 (cons 1337 nil))]
    (is (= 42   (first s)))
    (is (= 1337 (first (next s))))
    (is (= nil  (next (next s))))))

;; Making a seq from collection:
;; clojure.core/seq function

(seq [1 2 3])                    ;=> (1 2 3)
(seq '(1 2 3))                   ;=> (1 2 3)
(seq {:name "foo" :role "bar"})  ;=> ([:role "bar"] [:name "foo"])
(seq #{\a \e \i \o \u \y})       ;=> (\a \e \i \o \u \y)
(seq "hello")                    ;=> (\h \e \l \l \o)

;; important, seq from empty collection produces nil:

(seq [])                         ;=> nil
(seq '())                        ;=> nil
(seq nil)                        ;=> nil

;; Idiomatic way to check is collection is empty:

(let [some-empty-collection []]
  (if (nil? (seq some-empty-collection))
    "Yeah, is empty"))

;; Clojure's = compares collections and sequences based on their content:

(deftest =-tests
  (is (= [1 2 3] '(1 2 3)))
  (is (= [1 2 3] (seq [1 2 3]))))

;; With this in mind, we can do some tests:

(deftest seq-tests
  (let [s (seq [1 2 3 4])]
    (is (= [0 1 2 3 4]  (cons 0 s)))
    (is (= 1            (first s)))
    (is (= [2 3 4]      (next s)))
    (is (= [3 4]        (next (next s))))
    (is (= [4]          (next (next (next s)))))
    (is (= nil          (next (next (next (next s))))))
    (is (= nil          (next (next (next (next (next s)))))))

    ;; as you can see, 'next' returns nil when the seq is exhausted. This means
    ;; that 'next' must check if the next cons cell is empty, and this means
    ;; some (usually very little) work.

    ;; Sometimes you do not wan't that work to be done, so there's the
    ;; 'rest' function:

    (is (= [2 3 4]      (rest s)))
    (is (= [3 4]        (rest (rest s))))
    (is (= [4]          (rest (rest (rest s)))))
    (is (= []           (rest (rest (rest (rest s))))))
    (is (= []           (rest (rest (rest (rest (rest s)))))))

    ;; As you can see, rest _always_ returns a seq.
    ;; Even here:
    (is (= [] (rest nil)))

    ;; Normally you'll use just 'first' and 'next'.

    ;; More seq functions: take, drop

    (is (= [1 2] (take 2 s)))
    (is (= [3 4] (drop 2 s)))
    (is (= [2 3] (take 2 (drop 1 s))))

    ;; take-while, drop-while

    (is (= [1 2] (take-while (fn [v] (< v 3)) s)))
    (is (= [3 4] (drop-while (fn [v] (< v 3)) s)))

    ;; filter, remove

    (is (= [1 3] (filter odd? s)))
    (is (= [2 4] (remove odd? s)))))

;; functions that take a seq call 'seq' on sequence argument, so these both work same way

(filter odd? (seq [1 2 3]))                                 ;=> (1 3)
(filter odd? [1 2 3])                                       ;=> (1 3)

;; Excercise:
;; ----------
;;
;; Here are some programming languages:

(def languages (seq [{:name "clojure"  :invented 2007}
                     {:name "java"     :invented 1995}
                     {:name "lisp"     :invented 1958}
                     {:name "c"        :invented 1972}]))

;; Make a function that takes languages and return those that were
;; invented before 1990.

(defn mature-langs [langs]
  )

(deftest mature-langs-tests
  (is (= [{:name "lisp" :invented 1958}
          {:name "c" :invented 1972}]
         (mature-langs languages))))

;; When working with seq, keep in mind, am I working with a collection, or a seq:
;;
;; core functions that work with data-structures:
;;   (func <data-structure> args)
;;
;; core functions that work with seq's:
;;   (func args <seq>)
;;
;; For example:
;; conj accepts data-structure and a value

(conj [] 1)                 ;=> [1]

;; cons accepts value and a seq

(cons 1 '())                ;=> (1)

;;;
;;; More sequences:
;;;

;; Sequences can be infinitely long:

(def natural-numbers (range))

;; Don't try to evaluate this:
;; (count natural-numbers)      ;=> the core of the sun will run out of hydrogen before this returns

;; sequences can be lazy

;; Make a lazy seq of random integers between 0 and 100

(defn random-ints []
  (cons (rand-int 101)
        (lazy-seq (random-ints))))

(comment
  (let [r (random-ints)]
    (println (take 10 r))))

;; Sequence of fibonacci numbers

(defn fibonacci
  ([]
   (concat [1 1] (fibonacci 1 1)))
  ([a b]
   (cons (+ a b) (lazy-seq (fibonacci b (+ a b))))))

(comment
  (let [f (fibonacci)]
    (println (take 10 f))))

;; You can check if (the tip of) a lazy sequence has already been "forced":

(defn heavy-work []
  (println "Doing some heavy work!")
  :heavy-results)

(defn lazy-worker []
  (lazy-seq (cons (heavy-work) (lazy-worker))))

(def lazy-results (lazy-worker))

(realized? lazy-results)                 ;=> false
(first lazy-results)                     ;=> :heavy-results (note the side effect)
(realized? lazy-results)                 ;=> true
(nth lazy-results 10)                    ;=> :heavy-results (note side effects)
