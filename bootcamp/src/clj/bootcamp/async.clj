(ns bootcamp.async
  (:require [clojure.core.async :refer [go <! <!! >! >!!] :as a]))

; Helper: logger

(defn logger
  "Returns a function that prints it's name, duration since this function was called, and its arguments to stderr"
  ([logger-name]
   (logger logger-name (System/currentTimeMillis)))
  ([logger-name start-time]
   (let [created     start-time
         logger-name (name logger-name)]
     (fn [& args]
       (let [millis     (- (System/currentTimeMillis) created)
             timestamp  (format "%.3f" (/ millis 1000.0))
             message    (apply str timestamp " [" logger-name "]: " (->> args (map pr-str) (interpose " ")))]
         (.println System/err message))))))

#_
(let [log (logger :foo)]
  (log "Hello")
  (Thread/sleep 100)
  (log "world"))

; stdout:
;   0.000 [foo]: "Hello"
;   0.101 [foo]: "world"

;;
;; Channels
;;

; - channel is an async connunication channel
; - created with (chan)
; - closed with (close!)

#_
(let [c (a/chan)]
  (println (type c))
  (a/close! c))

; stdout:
;   clojure.core.async.impl.channels.ManyToManyChannel

; Channels are very light-weight:
; Create (and garbage-collect) 1 million channels

#_
(time
 (dotimes [_ 1e6]
   (a/chan)))

; stdout:
; "Elapsed time: 51.389819 msecs"

;
; Writing to and reading from channel:
;

#_
(let [c      (a/chan)
      reader (logger :reader)
      writer (logger :writer)]
  (future
    ; This is executed in another thread
    (reader "Reading from channel")
    (reader "Received" (<!! c))
    (reader "Done"))
  (writer "Sleeping for 1 sec...")
  (Thread/sleep 1000)
  (writer "Writing to channel")
  (>!! c "Hello")
  (writer "Done"))

; stdout:
;   0.000 [reader]: "Reading from channel"
;   0.000 [writer]: "Sleeping for 1 sec..."
;   1.001 [writer]: "Writing to channel"
;   1.001 [reader]: "Received" "Hello"
;   1.001 [reader]: "Done"
;   1.002 [writer]: "Done"
;
; Note how the reader was blocked until value was available.

#_
(let [c      (a/chan)
      reader (logger :reader)
      writer (logger :writer)]
  (future
    (writer "Writing to channel")
    (>!! c "Hello")
    (writer "Done"))
  (reader "Sleeping for 1 sec...")
  (Thread/sleep 1000)
  (reader "Reading from channel")
  (reader "Received" (<!! c))
  (reader "Done"))

; stdout:
;   0.000 [reader]: "Sleeping for 1 sec..."
;   0.000 [writer]: "Writing to channel"
;   1.003 [reader]: "Reading from channel"
;   1.004 [reader]: "Received" "Hello"
;   1.004 [writer]: "Done"
;   1.004 [reader]: "Done"
;
; Now the writer was blocked until reader was available.

; Closing channels with clojure.core.async/close!

#_
(let [c (a/chan)]
  (a/close! c)
  (println "reading from closed:" (<!! c))
  (println "writing to closed:" (>!! c "hello")))

; stdout:
;   reading from closed: nil
;   writing to closed: false

;
; Buffering:
;

; - Channel created with (chan) does not have any buffering
; - Both reader and the writer must rendezvous at the chan
; - Channel can be created with a buffer

; Channel with buffering

#_
(let [c      (a/chan 2) ; <= NOTE: Channel with buffer with room for 2 items
      reader (logger :reader)
      writer (logger :writer)]
  (future
    (writer "Writing to channel")
    (>!! c "Hello")
    (>!! c "world")
    (a/close! c)
    (writer "Done"))
  (reader "Sleeping for 1 sec...")
  (Thread/sleep 1000)
  (reader "Reading from channel")
  (reader "Received 1" (<!! c))
  (reader "Received 2" (<!! c))
  (reader "Received 3" (<!! c))
  (reader "Done"))

; stdout:
;   0.000 [reader]: "Sleeping for 1 sec..."
;   0.000 [writer]: "Writing to channel"
;   0.000 [writer]: "Done"
;   1.003 [reader]: "Reading from channel"
;   1.004 [reader]: "Received 1" "Hello"
;   1.004 [reader]: "Received 2" "world"
;   1.004 [reader]: "Received 3" nil
;   1.004 [reader]: "Done"
;
; Note how the writer was able to write two items to channel without blocking.

; Next examples are easier with an utility method that reads and prints values
; from channel until channel is closed.

(defn read-until-closed!! [c log]
  (log "Reading until closed")
  (loop []
    (when-let [v (<!! c)]
      (log v)
      (recur)))
  (log "Done"))

;
; Dropping and sliding buffers:
;

; Channel with dropping-buffer
; - if buffer is full, new values are "dropped"

#_
(let [c (a/chan (a/dropping-buffer 10)) ; buffer to 10 items
      reader-log (logger :reader)
      writer-log (logger :writer)]
  (writer-log "Write 20 numbers and close channel")
  (dotimes [n 20]
    (>!! c n))
  (a/close! c)
  (writer-log "Done")
  (read-until-closed!! c reader-log))

; stdout:
;   0.000 [writer]: "Write 20 numbers and close channel"
;   0.001 [writer]: "Done"
;   0.001 [reader]: "Reading until closed"
;   0.002 [reader]: 0
;   0.002 [reader]: 1
;   0.002 [reader]: 2
;   0.002 [reader]: 3
;   0.002 [reader]: 4
;   0.002 [reader]: 5
;   0.002 [reader]: 6
;   0.002 [reader]: 7
;   0.002 [reader]: 8
;   0.002 [reader]: 9
;   0.002 [reader]: "Done"
;
; Note that writer was able to write 20 items, even when buffer had room for only 10. After
; the buffer was full writer was not blocked. The values were just dropped.

; Channel with sliding-buffer
; - if buffer is full, oldest values are dropped

#_
(let [c (a/chan (a/sliding-buffer 10))
      reader-log (logger :reader)
      writer-log (logger :writer)]
  (writer-log "Write 20 numbers and close channel")
  (dotimes [n 20]
    (>!! c n))
  (a/close! c)
  (writer-log "Done")
  (read-until-closed!! c reader-log))

; stdout:
;   0.000 [writer]: "Write 20 numbers and close channel"
;   0.001 [writer]: "Done"
;   0.001 [reader]: "Reading until closed"
;   0.001 [reader]: 10
;   0.001 [reader]: 11
;   0.001 [reader]: 12
;   0.002 [reader]: 13
;   0.002 [reader]: 14
;   0.002 [reader]: 15
;   0.002 [reader]: 16
;   0.002 [reader]: 17
;   0.002 [reader]: 18
;   0.002 [reader]: 19
;   0.002 [reader]: "Done"
;
; Again writer was able to write 20 items, but this time it was the oldest values that were
; dropped.

;;
;; Go block
;;

; - The >!! and <!! block calling thread
; - clojure.core/future creates (potentially) new thread
; - Not available in cljs (no threads)
; - go blocks to the rescue
; - go block are executed by a threads from a thread pool
; - when execution would be blocked, it is "parked"
; - when execution is parked the thread can continue with another block
; - potentially huge number of go blocks can be served with small number of threads, possibly by just one

; blocks are also guite light-weight:

#_
(time
  (dotimes [n 1e6]
    (go)))

; stdout:
;   "Elapsed time: 302.193577 msecs"

; Inside go blocks, use "parking" functions >! and <!

#_
(let [c (a/chan)
      reader-log (logger :reader)
      writer-log (logger :writer)]
  (go
    (reader-log "Received" (<! c))) ; <! will park the block
  (writer-log "Sleeping for 1 sec...")
  (Thread/sleep 1000)
  (>!! c :hello)) ; out side of go block use >!!

; stdout:
;   0.000 [writer]: "Sleeping for 0.1 sec..."
;   1.002 [reader]: "Received" :hello

; Timeout channel:
; - channel that closes automatically after timeout

#_
(let [c      (a/chan)
      reader-log (logger :reader)
      writer-log (logger :writer)]
  (go
    (reader-log "Received" (<! c)))
  (go
    (writer-log "Sleeping for 1 sec...")
    (<! (a/timeout 1000))
    (writer-log "Timeout elapsed, sending message")
    (>! c :hello)))

; stdout:
;   0.003 [writer]: "Sleeping for 1 sec... ""
;   1.006 [writer]: "Timeout elapsed, sending message"
;   1.007 [reader]: "Received" :hello

;
; go returns a channel
;

#_
(let [c1 (a/chan)
      c2 (go
           (* 2 (<! c1)))
      reader-log (logger :reader)]
  (go
    (reader-log "Received 1:" (<! c2))
    (reader-log "Received 2:" (<! c2)))
  (go
    (>! c1 21)))

; stdout:
;   0.002 [reader]: "Received" 42
;   0.002 [reader]: "Received" nil

;;
;; Coordinating work with multiple channels:
;;

; alts! and alts!!

#_
(let [c1 (a/chan)
      c2 (a/chan)
      c-name {c1 "c1"
              c2 "c2"}]
  (go
    (<! (a/timeout (rand-int 1000)))
    (>! c1 "foo"))
  (go
    (<! (a/timeout (rand-int 1000)))
    (>! c2 "bar"))
  (let [[v c] (a/alts!! [c1 c2])]
    (str "Got " v " from " (c-name c))))

;=> "Read foo from c1"
; and sometimes
;=> "Read bar from c2"

; alt! and alt!!

#_
(let [go1-log (logger "go 1")
      go2-log (logger "go 2")
      c1 (a/chan)
      c2 (a/chan)
      c3 (a/chan)]
  (go
    (while true
      (go1-log (a/alt!
                 c1 ([v] (+ v 10))
                 c2 ([v] (* v 10))
                 [[c3 42]] (println "wrote to c3")))))
  (go
    (go2-log "writing to c1...")
    (>! c1 5)
    (go2-log "writing to c2...")
    (>! c2 5)
    (go2-log "reading from c2...")
    (go2-log "got from c3: " (<! c3))))

;;
;; Remedy for callback hell:
;;

#_
(defn time-consuming-task-with-callback [callback]
  (Thread/sleep 1000) ; simulate hard work...
  (callback 42))      ; deliver results

#_
(time-consuming-task-with-callback (fn [response]
                                     (println "Response:" response)))

#_
(defn time-consuming-task-with-ch []
  (let [c (a/chan 1)]
    (go
      (Thread/sleep 1000) ; simulate hard work...
      (>! c 42)           ; deliver results
      (a/close! c))
    c))

#_
(go (println "Repsonse:" (<! (time-consuming-task-with-ch))))

#_
(go (a/alt!
      (time-consuming-task-with-ch) ([response]
                                      (println "Response:" response))
      (a/timeout 500)               ([_]
                                      (println "Timeout"))))

;; Event-loop

#_
(defn start-loop []
  (let [ctrl   (a/chan)
        events (a/chan)
        log    (logger :loop)]
    (go
      (loop []
        (a/alt!
          events ([v]
                   (when v
                     (log "received" v)
                     (recur)))
          ctrl   ([]
                   (log "close requested")
                   nil)
          (a/timeout 100) ([]
                            (log "timeout")
                            (recur))))
      (log "closed"))
    [ctrl events]))

#_
(let [[ctrl events] (start-loop)]
  (go (>! events 1))
  (go (>! events 2))
  (go (>! events 3))
  (Thread/sleep 250)
  (go (>! events 4))
  (go (a/close! ctrl))
  nil)

; stdout
;  0.002 [loop]: "received" 1
;  0.003 [loop]: "received" 2
;  0.003 [loop]: "received" 3
;  0.103 [loop]: "timeout"
;  0.208 [loop]: "timeout"
;  0.256 [loop]: "received" 4
;  0.257 [loop]: "close requested"
;  0.257 [loop]: "closed"
;
; Note that the order of "received" messages may differ.

;;
;; (semi) Practical example: news aggregator
;; - Fetch news from multiple sources
;; - Combine results
;;

#_
(require '[clj-http.client :as http])
#_
(require '[net.cgrand.enlive-html :as html])

; First a helper that fetch selected part from html resource:

#_
(defn fetch [url selector]
  (-> (http/get url {:as :stream})
      :body
      html/html-resource
      (html/select selector)))

; Fetch hacker-news links:

#_
(defn hacker-news-links []
  (let [c (a/chan 1)]
    (future
      (->> (fetch "https://news.ycombinator.com" [:td.title html/content])
           (map (juxt (comp first :content) (comp :href :attrs)))
           (filter (partial every? string?))
           (butlast)
           (map (partial cons 'hn))
           (map (partial a/put! c))
           (dorun))
      (a/close! c))
    c))

; Fetch reddit links:

#_
(defn reddit-links []
  (let [c (a/chan 1)]
    (future
      (->> (fetch "http://www.reddit.com" [:#siteTable :> :.thing :p.title :> :a])
           (map (juxt (comp first :content) (comp :href :attrs)))
           (map (partial cons 'reddit))
           (map (partial a/put! c))
           (dorun))
      (a/close! c))
    c))

; Create channel for hacker-news links and another for reddit links, merge them to one
; channel. Map channel content so that we get just the news title (first element).
; Print results.

#_
(read-until-closed!!
  (a/map (fn [[source title _]]
           (str "(" source ") " title))
         [(a/merge [(hacker-news-links) (reddit-links)])])
  (logger "news"))

; stdout:
;   0.000 [news]: "Reading until closed"
;   0.592 [news]: "(hn) Pardon Snowden"
;   0.592 [news]: "(hn) Introducing the Firefox debugger.html"
;   0.592 [news]: "(hn) Announcing Envoy: C++ L7 proxy and communication bus"
;   0.592 [news]: "(hn) Matt Stone and Trey Parker Reveal the Secret to Keeping South Park Cool"
;   0.593 [news]: "(hn) GitHub Universe – Live Stream"
;   0.593 [news]: "(hn) Teen Creates “Sit with Us” App for Bullied Kids"
;   0.593 [news]: "(hn) All New Amazon Echo Dot"
;   0.593 [news]: "(hn) Uber starts self-driving car pickups in Pittsburgh"
; ...
;   1.112 [news]: "(reddit) Why, Microsoft?"
;   1.112 [news]: "(reddit) It's not the hangover Radar"
;   1.112 [news]: "(reddit) My cat likes to have a \"cocktail\" whenever I have a drink (she meows until she gets it) and she'll only drink out of this festive glass"
;   1.112 [news]: "(reddit) This box of limes recommends a different way of cutting fruit."
;   1.113 [news]: "(reddit) TIL The world's largest concentration of nukes is housed 20 miles NW of Seattle, and it's defended by trained dolphins."
;   1.114 [news]: "(reddit) Hello world!"

;;
;; Bonus:
;;
;;   clojure.core.async/map      - map channel(s)
;;   clojure.core.async/merge    - combine two ore more channels to one
;;   clojure.core.async/mult     - distribute items to multiple channels
;;   clojure.core.async/mix      - combine multiple channels to one
;;
;; API Docs: http://clojure.github.io/core.async/
;; also, see http://clojuredocs.org/clojure.core.async
;;
