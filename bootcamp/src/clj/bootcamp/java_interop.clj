(ns bootcamp.java-interop
  (:import [java.util Random]
           [java.io File FilenameFilter]))

;;
;; Java inter-op
;; -------------
;;
;; - Import
;; - Instantiate Java objects.
;; - Invoke methods on Java objects
;; - Clojure utils: class, instance?, doto
;; - exception handling
;; - Create objects that implement Java interfaces
;;

; Instantiate Java objects:

(def rnd (new Random 42))                                   ; old way
(def rnd (Random. 42))                                      ; idiomatic

; Invoke method:

(. rnd nextInt 1337)                                        ;=> 897  old way
(.nextInt rnd 1337)                                         ;=> 152  idiomatic

; class

(class rnd)                                                 ;=> java.util.Random
(class (class rnd))                                         ;=> java.util.Class

; instance?

(instance? Random rnd)                                      ;=> true
(instance? Random 42)                                       ;=> false

; Static methods/fields:

(str "Java version: " (System/getProperty "java.version"))  ;=> "Java version: 1.8.0_45"
(str "AC/DC: Back in " java.awt.Color/BLACK)                ;=> "AC/DC: Back in java.awt.Color[r=0,g=0,b=0]"

;;
;; Exceptions:
;;

(comment
  (try
    (println "Here we go...")
    (throw (RuntimeException. "Oh no!"))
    (catch Exception e
      (println "Got exception:" (.getMessage e)))
    (finally
      (println "That's it for exceptions"))))

; stdout
;   Here we go...
;   Got exception: Oh no!
;   That's it for exceptions

;
; Clojure functions implement java.lang.Runnable and java.util.concurrent.Callable.
;

(comment
  (let [t (Thread. (fn []
                     (Thread/sleep 1000)
                     (println "Done")))]
    (.start t)))

;=> nil
; after 1 sec prints "Done" to stdout.

;
; proxy: Implement Java interface:
;

(comment
  (doseq [f (-> (File. ".")
                (.listFiles (proxy [FilenameFilter] []
                              (accept [dir file-name]
                                (.endsWith file-name ".clj")))))]
    (println "file:" (.getName f))))
; prints:
;  file: project.clj
