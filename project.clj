(defproject gufovolante "0.1.0-SNAPSHOT"
  :description "Terminale interattivo di analisi soldipubblici.gov.it"
  :url "http://dcentproject.eu"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ^:replace [[org.clojure/clojure "1.8.0"]
                           [org.clojure/data.json "0.2.6"]
                           [org.clojure/data.csv "0.1.3"]

                           [org.apache.commons/commons-compress "1.12"]
                           [org.tukaani/xz "1.5"]

                           [clj-http "3.1.0"]
                           [cheshire "5.6.3"]

                           [clojure-humanize "0.2.0"]

                           [clojure-csv/clojure-csv "2.0.2"]
                           [semantic-csv "0.1.0"]

                           ;; [gorilla-repl "0.3.6"]
                           ;; [incanter-gorilla "0.1.0"]

                           [huri "0.5.0-SNAPSHOT"]

                           ;; gorilla-repl deps
                           [http-kit "2.1.19"]
                           [ring/ring-json "0.4.0"]
                           [compojure "1.5.1"]
                           [org.slf4j/slf4j-api "1.7.21"]
                           [ch.qos.logback/logback-classic "1.1.7"]
                           [gorilla-renderable "2.0.0"]
                           [gorilla-plot "0.1.4"]
                           [javax.servlet/servlet-api "2.5"]
                           [grimradical/clj-semver "0.3.0" :exclusions [org.clojure/clojure]]
                           [cider/cider-nrepl "0.12.0"]
                           [org.clojure/tools.nrepl "0.2.12"]
                           ]
  :source-paths ["src"]
  :resource-paths ["resources"]

  :main ^:skip-aot gorilla-repl.core
  :profiles {:uberjar {:aot [gorilla-repl.core gufovolante.core]}}
  :target-path "target/%s"
  ;; :plugins [[lein-gorilla "0.3.6"]]
  ;; :profiles {:uberjar {:aot :all}}
  )
