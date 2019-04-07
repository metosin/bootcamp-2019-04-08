(ns bootcamp.data.books)

(def authors {:fogus     {:fname "Michael"    :lname "Fogus"}
              :houser    {:fname "Chris"      :lname "Houser"}
              :cesarini  {:fname "Francesco"  :lname "Cesarini"}
              :thompson  {:fname "Simon"      :lname "Thompson"}
              :rochester {:fname "Eric"       :lname "Rochester"}
              :venkat    {:fname "Venkat"     :lname "Subramaniam"}
              :friedman  {:fname "Daniel"     :lname "Friedman"}
              :felleisen {:fname "Matthias"   :lname "Felleisen"}
              :sussman   {:fname "Gerald"     :lname "Sussman"}
              :abelson   {:fname "Harold"     :lname "Abelson"}
              :jsussman  {:fname "Julie"      :lname "Sussman"}})

(def books [{:title   "The Joy of Clojure"
             :langs   #{:clojure}
             :pages   328
             :authors [:fogus :houser]}
            {:title   "Erlang Programming"
             :langs   #{:erlang}
             :pages   470
             :authors [:cesarini :thompson]}
            {:title   "Clojure Data Analysis Cookbook"
             :langs   #{:clojure}
             :pages   326
             :authors [:rochester]}
            {:title   "Programming Concurrency on the JVM"
             :langs   #{:java :ruby :groovy :scala :clojure}
             :pages   270
             :authors [:venkat]}
            {:title   "The Little Schemer"
             :langs   #{:scheme}
             :pages   196
             :authors [:friedman :felleisen :sussman]}
            {:title   "Types and Programming Languages"
             :langs   #{:haskel :java :fortran}
             :pages   623
             :authors [:abelson :sussman :jsussman]}])

; source: Metosin library

