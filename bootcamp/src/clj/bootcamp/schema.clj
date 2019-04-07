(ns bootcamp.schema
  (:require [clojure.test :refer :all]
            [schema.core :as s]
            [schema.coerce :as sc]
            [schema.utils :as su]
            [cheshire.core :as json]
            [bootcamp.data.books :as b]))

;; Schemas - https://github.com/Prismatic/schema

; Checking by type:

(s/check Long 42)                                           ;=> nil
(s/check java.util.Date "bar")                              ;=> (not (instance? java.util.Date "bar"))

(s/validate Long 5)                                         ;=> 5

(try
  (s/validate java.util.Date "bar")
  (catch Exception e
    e))
;=> #error{:cause "Value does not match schema: (not (instance? java.util.Date \"bar\"))", ...


; Checking a collection:

(s/check [String] ["a" "5" "b"])                            ;=> nil

(s/check {:a Long, :b String}
         {:a 42})
;=> {:b missing-required-key}

; schema.core ns has loads of helpers / aliases:

(s/check s/Num 5)                                           ;=> nil
(s/check s/Str "foo")                                       ;=> nil

; Those are especially important when sharing code with Clojure and ClojureScript:
; For example, s/Str evaluates to java.lang.String in Clojure and String in ClojureScript.

; Custom predicates:

(s/check
  (s/pred (fn [v] (= v "foobar")))
  "barfoo")
;=> (not (#object[bootcamp.schema$eval26685$fn__26686 0x6578211 "bootcamp.schema$eval26685$fn__26686@6578211"] "barfoo"))

; add helpful symbol for predicate for more readable error messages:

(s/check
  (s/pred (fn [v] (= v "foobar")) 'not-foobar)
  "barfoo")
;=> (not (not-foobar "barfoo"))

; Something which implements Schema-protocol.
; Schema.core provides multiple utilities:

(s/check (s/eq "foobar") "barfoo")                          ;=> (not (= "foobar" "barfoo"))
(s/check (s/maybe s/Int) nil)                               ;=> nil
(s/check (s/either s/Str s/Int) "foo")                      ;=> nil
(s/check (s/both s/Int (s/pred even? 'not-even)) 5)         ;=> (not (not-even 5))
(s/check (s/enum :foo :bar) :bar)                           ;=> nil

;
; Excercise:
; ----------
;

; Check the bootcamp.data.books ns, then fix this schema:

(s/defschema Author {:fname s/Str})

; ...so that this test passes:

(deftest Author-schema-tests
  (is (every? nil? (map (partial s/check Author) (vals b/authors)))))

; Fix this schema too:

(s/defschema Book {:title   s/Str
                   :langs   #{s/Str}
                   :pages   s/Int
                   :authors s/Keyword})

(deftest Book-schema-tests
  (is (every? nil? (map (partial s/check Book) b/books))))

;;
;; Coercing:
;;

; Often data is not in expected form, but can be coerced. For exampme, JSON
; does not have keywords, sets, or dates.

(-> b/books
    (first)
    (json/encode))
;=> {"title":    "The Joy of Clojure",
;    "langs":    ["clojure"],
;    "pages":    328,
;    "authors":  ["fogus","houser"]}

(-> b/books
    (first)
    (json/encode)
    (json/decode))
;=> {"title"    "The Joy of Clojure",
;    "langs"    ["clojure"],
;    "pages"    328,
;    "authors"  ["fogus" "houser"]}

(-> b/books
    (first)
    (json/encode)
    (json/decode keyword))
;=> {:title    "The Joy of Clojure",
;    :langs    ["clojure"],
;    :pages    328,
;    :authors  ["fogus" "houser"]}

(s/check Book (-> b/books
                  (first)
                  (json/encode)
                  (json/decode keyword)))
;=> {:langs (not (set? ["clojure"])),
;    :authors [(not (keyword? "fogus"))
;              (not (keyword? "houser"))]}

;
; Coercers to the rescue!
;

; Let's make a string->keyword coercer:

(defn string->keyword-coercer [schema]
  (if (= schema s/Keyword)                                  ; if schema says it accepts a keyword,
    (fn [value]                                             ; ...then use this corecion function
      (if (string? value)                                   ; if freceived value is a string...
        (keyword value)                                     ; ...then convert it to keyword
        value))))                                           ; ...else return the value as is

; Make a coercion function:

(def coerce-fn (sc/coercer {:foo s/Keyword} string->keyword-coercer))

(coerce-fn {:foo "bar"})                                    ;=> {:foo :bar}

;
; Excercise:
; ----------
; Make a coercer for vector -> set
;

; hints:
(set? #{"a" "b"})                                           ;=> true
(vector? ["a" "b"])                                         ;=> true
(set ["a" "b"])                                             ;=> #{"a" "b"}

; Implement this:

(defn vector->set-coercer [schema]
  )

; so that these tests pass:

(deftest vector->set-coercer-tests
  (let [data      {:foo ["a" "b"]}
        schema    {:foo #{s/Str}}
        coerce-fn (sc/coercer schema vector->set-coercer)]
    (is (not (su/error? (coerce-fn data))))
    (is (= {:foo #{"a" "b"}} (coerce-fn data)))))

;
; Coercers can ne combined:
;

(def ->book (sc/coercer Book (fn [schema]
                               (or
                                 (string->keyword-coercer schema)
                                 (vector->set-coercer schema)))))

(-> b/books
    (first)
    (json/encode)
    (json/decode keyword)
    (->book))
;=> {:title    "The Joy of Clojure",
;    :langs    #{:clojure},
;    :pages    328,
;    :authors  [:fogus :houser]}


;
; NOTE: schema.coerce ns has json-coercion-matcher which offers
; existing implementation for e.g. keyword and sets matchers:
;
; So all your hard work above can be replaced with:
;   (def ->book (sc/coercer Book sc/json-coercion-matcher))
;
