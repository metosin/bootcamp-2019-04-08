(defproject bootcamp3 "0.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]

                 ; Common libs:
                 [prismatic/schema "1.1.10"]
                 [prismatic/plumbing "0.5.5"]
                 [metosin/schema-tools "0.11.0"]
                 [metosin/spec-tools "0.9.1"]
                 [metosin/potpuri "0.5.2"]

                 ; Ring:
                 [ring/ring-core "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-jetty-adapter "1.7.1"]

                 ; Rest API:
                 [metosin/compojure-api "1.1.12"]
                 [metosin/ring-http-response "0.9.1"]

                 ; Swagger UI
                 [metosin/ring-swagger-ui "3.20.1"]

                 ; Rest API:
                 [metosin/compojure-api "2.0.0-alpha18"]
                 [metosin/ring-http-response "0.9.1"]
                 [metosin/muuntaja "0.6.4"]

                 ; jsonista
                 [metosin/jsonista "0.2.2"]

                 ; Database
                 [hikari-cp "2.7.1"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [org.flywaydb/flyway-core "5.2.4"]
                 [com.layerware/hugsql "0.4.9"]

                 ; HTTP and HTML
                 [clj-http "3.9.1"]
                 [hiccup "1.0.5"]
                 [enlive "1.1.6"]

                 ; Async:
                 [org.clojure/core.async "0.4.490"]

                 ; ClojureScript:
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/tools.reader "1.3.2"]
                 [prismatic/dommy "1.1.0"]

                 ; Logging:
                 [org.clojure/tools.logging "0.5.0-alpha.1"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]]

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"]
  :java-source-paths ["src/java"]
  :main bootcamp.main

  :plugins [[metosin/bat-test "0.4.2"]]

  :profiles {:dev {:resource-paths ["target/generated"]
                   :plugins [[lein-cljsbuild "1.1.7"]]}
             :uberjar {:main  bootcamp.main
                       :aot   [bootcamp.main]
                       :uberjar-name "bootcamp.jar"}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler     {:main            "bootcamp.hello-cljs-world"
                                       :asset-path      "js/out"
                                       :output-to       "target/generated/public/js/bootcamp.js"
                                       :output-dir      "target/generated/public/js/out"
                                       :source-map      true
                                       :optimizations   :none
                                       :cache-analysis  true
                                       :pretty-print    true}}]})
