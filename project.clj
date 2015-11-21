(defproject cons "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure "1.7.0"]
    [cheshire "5.5.0"]
    [clj-http "2.0.0"]
    [clj-kafka "0.3.3"]
    [clj-time "0.11.0"]
    [com.novemberain/monger "3.0.1"]
    [org.clojure/tools.logging "0.3.1"]
  ]
  :main ^:skip-aot cons.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
