(defproject sauna-iot "0.1.0-SNAPSHOT"

  :description "A simple full-stack app example for demonstrating Clojure(script)"

  :url "https://github.com/metosin/sauna-iot"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.0"]

                 ;; Time & date
                 [clj-time "0.15.1"]

                 ;; JSON serialization
                 [cheshire "5.8.1"]

                 ;; Database
                 [org.postgresql/postgresql "42.2.5"]
                 [conman "0.8.3"]
                 [luminus-migrations "0.6.5"]

                 ;; Component system
                 [mount "0.1.15"]

                 ;; Configuration
                 [cprop "0.1.13"]

                 ;; Development dependencies
                 [org.clojure/tools.namespace "0.2.11"]]

  :main ^:skip-aot backend.main

  :source-paths ["src/clj" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :target-path "target/%s"

  :repl-options {:init-ns user}

  :profiles {:dev {:resource-paths ["target/dev/resources"]}
             :prod {:resource-paths ["target/prod/resources"]}
             :uberjar {:uberjar-name "app.jar"
                       :aot [backend.main]}}

  :plugins [[metosin/boot-alt-test "0.4.0-SNAPSHOT"]]

  :alt-test {:report :pretty}

  :auto-clean false

  :aliases {"prod" ["with-profile" "prod" "do" "clean" "uberjar"]})
