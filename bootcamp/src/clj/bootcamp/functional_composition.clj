(ns bootcamp.functional-composition
  (:require [clojure.test :refer :all]
            [clojure.string :as string]))

;;
;; Functional composition:
;;

; partial

(defn greeter [message your-name]
  (str message ", " your-name))

(def greeter-en (partial greeter "Hello"))
(def greeter-fi (partial greeter "Moi"))

(greeter-en "world")                                        ;=> "Hello, world"
(greeter-fi "maailma")                                      ;=> "Moi, maailma"

; comp

; Make a function that accepts a seq of strings and returns
; the strings joined with ", " in upper-case letters:

(string/join ", " ["a" "b"])                                ;=> "a, b"
(string/upper-case "hello")                                 ;=> "HELLO"

(def shout (comp string/upper-case
                 (partial string/join ", ")))

(shout ["this" "is" "fun"])                                 ;=> "THIS, IS, FUN"

; Exercise:
; Make a function that accepts a string of digits, converts it to
; a number and returns the number doubled:

(defn str->long [v]
  (Long/parseLong v))

(str->long "42")                                            ;=> 42

; Fix this
(def str-doubler str->long)

; This should pass
(deftest str-doubler-test
  (is (= 42 (str-doubler "21"))))
