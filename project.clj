(defproject gufovolante "0.1.0-SNAPSHOT"
  :description "Terminale interattivo di analisi soldipubblici.gov.it"
  :url "http://dcentproject.eu"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.csv "0.1.3"]

                 [org.apache.commons/commons-compress "1.12"]
                 [org.tukaani/xz "1.5"]

                 [clj-http "3.1.0"]
                 [cheshire "5.6.3"]

                 [clojure-humanize "0.2.0"]

                 [clojure-csv/clojure-csv "2.0.2"]
                 [semantic-csv "0.1.0"]

                 [gorilla-repl "0.3.6"]
                 [huri "0.5.0-SNAPSHOT"]
                 ]
  :main ^:skip-aot gorilla-repl.core
  :target-path "target/%s"
  :plugins [[lein-gorilla "0.3.6"]]
  :profiles {:uberjar {:aot :all}}
  )
