(ns bootcamp.meta-proramming
  (:require [clojure.test :refer :all]
            [bootcamp.data.books :as b]))

;
; Meta programming:
;

; defn is a macro, use macroexpan to reveal how it works:

(macroexpand-1 '(defn foo [x] (+ x 5)))
;=> (def foo (clojure.core/fn ([x] (+ x 5))))

;
; Roll your own:
;

(defmacro infix->prefix [a op b]
  (list op a b))

(macroexpand-1 '(infix->prefix 5 + 6))                      ;=> (+ 5 6)

(infix->prefix 5 + 6)                                       ;=> 11

;
; Regular function evaluation order:
;

(+ (* 4 10) (inc 1))                                        ;=> 42

; Evaluated:
; (+ (* 4 10) (inc 1))
;    \------/
;       |
;       |
; (+    40    (inc 1))
;             \-----/
;                |
;                |
; (+    40       2   )
; \------------------/
;          |
;          |
;          42
;

; Macros are functions that are evaluated while "going down" and
; thet get the s-expressions as arguments:

(infix->prefix (* 4 10) + (inc 1))                          ;=> 42
; (infix->prefix (* 4 10) + (inc 1))
; \--------------------------------/
;               |
;               |    a   = (* 4 10)
;               |    op  = +
;               |    b   = (inc 1)
;               |    returns: (op a b)
;               |
;       (+ ( 4 10) (inc 1))
;          \-----/
;             |
;             |
;       (+   40    (inc 1))
;                  \-----/
;                     |
;                     |
;       (+   40       2   )
;       \-----------------/
;                |
;                |
;                42

(+ (infix->prefix 4 * 10) (inc 1))                          ;=> 42

; (+ (infix->prefix 4 * 10) (inc 1))
; (+ \--------------------/ (inc 1))
;              |
;              |  a  = 4
;              |  op = *
;              |  b  = 10
;              |
; (+        (* 4 10)        (inc 1))
; (+        \------/        (inc 1))
;              |
;              |
; (+           40           (inc 1))
; (+           40           \-----/)
;                              |
;                              |
; (+           40              2   )
; \--------------------------------/
;               |
;               |
;               42


;;
;; Java 7 has now the "try with resources":
;;
;;   try (BufferedReader br = new BufferedReader(new FileReader(path))) {
;;     return br.readLine();
;;   }
;;
;; It only took ~15 years to make it.
;;

;; Same idiom in Clojure:

; (let [resource ...make-resource...]
;   (try
;     ...use-resource...
;     (finally
;       (.close reource))))

(defmacro try-with-resource [resource make-resource & use-resource]
  `(let [~resource ~make-resource]
     (try
       ~@use-resource
       (finally
         (.close ~resource)))))

(macroexpand-1 '(try-with-resource r (open-some-file)
                  (do-some-stuff r)
                  (work-with-file r)
                  (heavy-stuff-here r)))
;=> (clojure.core/let [r (open-some-file)]
;     (try
;       (do-some-stuff r)
;       (work-with-file r)
;       (heavy-stuff-here r)
;       (finally
;         (.close r))))

; Note:
;   Clojure already has with-open that does this (better).
;   See http://clojure.github.io/clojure/clojure.core-api.html#clojure.core/with-open

;;
;; Threading macros:
;;

(reduce + (map :pages (filter (comp :clojure :langs) b/books))) ;=> 924

; Joda talk: "Strong is Vader. Mind what you have learned. Save you it can."
; Use threading macros to translate Joda talk to human talk:

(->> b/books (filter (comp :clojure :langs)) (map :pages) (reduce +)) ;=> 924

(->> b/books
     (filter (comp :clojure :langs))
     (map :pages)
     (reduce +))                                            ;=> 924

; How the work:

(macroexpand-1 '(->  X (f1) (f2)))                          ;=> (f2 (f1 X))
(macroexpand-1 '(->> X (f1) (f2)))                          ;=> (f2 (f1 X))

(macroexpand-1 '(->  X (f1 a) (f2 b c)))                    ;=> (f2 (f1 X a) b c)
(macroexpand-1 '(->> X (f1 a) (f2 b c)))                    ;=> (f2 b c (f1 a X))

; Rule of thumb:
; Working with a collection, use ->
; Working with a seq, use ->>

(-> {:title "Murach's Mainframe COBOL"
     :langs #{}
     :read? false}
    (assoc :pages 687)
    (update-in [:langs] conj :cobol)
    (dissoc :read?))
;=> {:title "Murach's Mainframe COBOL", :langs #{:cobol}, :pages 687}

(->> (range 10)
     (filter odd?)
     (map (partial * 2))
     (reduce +))
;=> 50

;
; some-> and some->>
;

(macroexpand-1 '(some-> X f1 f1))
;=> (clojure.core/let [G__29557 X
;                      G__29557 (if (clojure.core/nil? G__29557)
;                                  nil
;                                  (clojure.core/-> G__29557 f1))
;                      G__29557 (if (clojure.core/nil? G__29557)
;                                  nil
;                                 (clojure.core/-> G__29557 f1))]
;    G__29557)

(->> b/books
     (filter (comp :clojure :langs))
     (first)
     :title
     (.toUpperCase))
;=> "THE JOY OF CLOJURE"

(try
  (->> b/books
       (filter (comp :cobol :langs))
       (first)
       :title
       (.toUpperCase))
  (catch Exception e
    (str e)))
;=> "java.lang.NullPointerException"

(some->> b/books
         (filter (comp :cobol :langs))
         (first)
         :title
         (.toUpperCase))
;=> nil

