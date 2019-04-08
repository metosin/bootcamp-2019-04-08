(ns bootcamp.data-types
  (:require [clojure.test :refer :all]))

;;
;; Handy type inspection functions:
;;   type       :- return the type of argument
;;   instance?  :- checks if arg2 is instance of arg1
;;

(type "hello")                                              ;=> java.lang.String
(type (type "hello"))                                       ;=> java.lang.Class

(instance? java.lang.String "hello")                        ;=> true
(instance? java.lang.String 42)                             ;=> false

;;;
;;; Basic data types:
;;;

(type 42)                                                   ;=> java.lang.Long
(type (int 42))
(type 3.12159)                                              ;=> java.lang.Double
(type (float 3.12159))
(type true)                                                 ;=> java.lang.Boolean
(type \x)                                                   ;=> java.lang.Character
(type #"foo\s+bar")                                         ;=> java.util.regex.Pattern
(type 3/14)                                                 ;=> clojure.lang.Ratio

;; Note: pay attention of unintended ratios:

(time
 (reduce + 1/2 (range 1000000)))
;;=> "Elapsed time: 396.400713 msecs"
;;   999999000001/2

;;
;; acc = 0.5
;; for x in range(1000000):
;;     acc += x
;;

(time
  (reduce + 0.5 (range 1000000)))
;;=> "Elapsed time: 16.039644 msecs"
;;    4.999995000005E11

;;;
;;; nil, same as null in Java et al.
;;;

nil                                                         ;=> nil

;;;
;;; Keywords:
;;;

:foo                                                        ;=> :foo
(= :foo :foo)                                               ;=> true
(keyword? :foo)                                             ;=> true
(keyword? "foo")                                            ;=> false
(keyword? (keyword "foo"))                                  ;=> true

;; The '=' function uses Object.equals
(= "foobar" (str "foo" "bar"))                              ;=> true

;; identical? uses Java == operator:
(identical? "foobar" (str "foo" "bar"))                     ;=> false

;; Equal keywords are always identical:
(identical? :foobar (keyword (str "foo" "bar")))            ;=> true

;;; Namespaces and keywords:

(namespace :foo)                                            ;=> nil
(namespace :foo/bar)                                        ;=> "foo"
(name :foo/bar)                                             ;=> "bar"

;; import clojure.string as s
;;
(require '[clojure.string :as s])

(namespace :s/foo)                                          ;=> "s"
(namespace ::s/foo)                                         ;=> "clojure.string"
(namespace ::foo)                                           ;=> "bootcamp.data-types"

;;;
;;; Symbols:
;;;

;; - Used to refer to something else
;; - Evaluate to that 'something'
;; - quote stops evaluation

(def answer 42)

answer                                                      ; 42
(quote answer)                                              ; answer
(symbol? (quote answer))                                    ; true
(type (quote answer))                                       ; clojure.lang.Symbol
(= (quote answer) (symbol "answer"))                        ; true
(identical? (quote answer) (symbol "answer"))               ; false

;; Reader macro '

(= (quote answer) 'answer)                                  ; true

;; Reader macro `

;; Same as ' but expands namespaces:

(namespace 'answer)                                         ;=> nil
(namespace `answer)                                         ;=> "bootcamp.data-types"

's/upper-case                                               ;=> s/upper-case
(namespace 's/upper-case)                                   ;=> "s"

`s/upper-case                                               ;=> clojure.string/upper-case
(namespace `s/upper-case)                                   ;=> "clojure.string

;; The ` is typically only used when writing macros.

;;;
;;; Functions:
;;;

(def say-hello (fn [your-name]
                 (str "Hello, " your-name)))

;; Conveniency macro for (def func-name (fn [args] body))
(defn say-hello [your-name]
  (str "Hello, " your-name))

(deftest say-hello-tests
  (is (= "Hello, world" (say-hello "world"))))

;; Multi-arity

(defn say-hello2
  ([]
   (say-hello2 "world"))
  ([your-name]
   (str "Hello, " your-name))
  ([greeting your-name]
   (str greeting ", " your-name)))


(deftest say-hello2-tests
  (is (= "Hello, world" (say-hello2)))
  (is (= "Hello, foo"   (say-hello2 "foo"))))

;; Function with any number of args:

(defn foo [a b & args]
  [a b args])

(foo 1 2 3 4)                                               ;=> [1 2 (3 4)]
(foo 1 2 3)                                                 ;=> [1 2 (3)]
(foo 1 2)                                                   ;=> [1 2 nil]
#_(foo 1)

;; Exercise:
;; ---------
;;
;; Write a function that returns the takes two arguments, the names of your
;; neighbours.  the function should returns the average length of those names
;; and your own name.
;;
;; Help:
;;
;; - Check functions count, + and / from clojure.core namespace
;; - Try: (clojure.repl/doc count)



;;;
;;; Closures:
;;;

(defn make-multiplier [multiplier]
  (fn [some-value]
    (* some-value multiplier)))

(def doubler (make-multiplier 2))
(def tripler (make-multiplier 3))

(deftest multiplier-tests
  (is (= 84  (doubler 42)))
  (is (= 126 (tripler 42))))

;; Exercise:
;; ---------
;;
;; This does not work, fix this so that the tests below succeed:

(defn greeter [message]
  (fn [your-name]


    ))

;; Hints: string?, (if ...), str

(deftest greeter-tests
  (let [f (greeter "Hello")]
    (is (= "Hello, world" (f "world")))
    (is (= nil (f nil)))))


;;; Note about evaluation order

;; (function arg1 arg2 arg3)

;; 12 + 23 + 45

(+ 12 23 45)

;; 12 + (12 / 3)

(+ 12 (/ 12 3))
