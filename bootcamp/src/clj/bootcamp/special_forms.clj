(ns bootcamp.special-forms
  (:require [clojure.test :refer :all]))

;; Define something to this namespace

(def answer 42)

;; Evaluate it:
answer                                                      ;=> 42

;; The 'answer' is now bound to value 42 in this namespace

;; We'll examine stuff using tests, like this one:

(deftest answer-tests
  (is (= 42 answer)))

;; Hint: try `(run-tests)` in the REPL

;;;
;;; Local bindings:
;;;

(let [a 21
      b 2]
  (str "a * b = " (* a b)))

(deftest let-tests
  (let [a 21
        b 2]
    (is (= 21 a))
    (is (= 2 b))
    (let [b 1337]
      (is (= 1337 b)))
    (is (= 2 b))))

;;
;; Fix this test:
;;

(deftest fix-these-let-tests
  (let [a "hello"
        b "world"]
    (is (= "Hello, world" (str a b)))))

;;;
;;; if
;;;

(deftest if-tests
  (is (= "Yes" (if true "Yes")))
  (is (= "No"  (if false "Yes" "No")))
  (is (= nil   (if false "Yes"))))

;; Exercise:
;; ---------
;;
;; Examine what is considered as 'true', change the "?" to "Yes" or "No"

(deftest truthy-tests
  (is (= "?"  (if true     "Yes" "No")))
  (is (= "?"  (if answer   "Yes" "No")))
  (is (= "?"  (if "hello"  "Yes" "No")))
  (is (= "?"  (if false    "Yes" "No")))
  (is (= "?"  (if nil      "Yes" "No"))))

;;;
;;; Use 'do' to evaluate multiple statements (always for side-effects)
;;;

(defn result []
  (println "result:" (if (= (* 2 21) answer)
                       (do
                         (println "Yes, we have the answer")
                         "yes")
                       (do
                         (println "Not, for some reason we do not have the answer")
                         "no"))))

;;;
;;;
;;;

(quote foo)
