(defproject gufovolante "0.1.0-SNAPSHOT"
  :description "Terminale interattivo di analisi soldipubblici.gov.it"
  :url "http://dcentproject.eu"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ^:replace [[org.clojure/clojure "1.8.0"]
                           [org.clojure/data.json "0.2.6"]
                           [org.clojure/data.csv "0.1.4"]

                           [org.apache.commons/commons-compress "1.16.1"]
                           [org.tukaani/xz "1.8"]

                           [clj-http "3.8.0"]
                           [cheshire "5.8.0"]

                           [clojure-humanize "0.2.2"]

                           [clojure-csv/clojure-csv "2.0.2"]
                           [semantic-csv "0.2.0"]

                           ;; [gorilla-repl "0.3.6"]
                           ;; [incanter-gorilla "0.1.0"]

                           [huri "0.5.0-SNAPSHOT"]

                           ;; gorilla-repl deps
                           [http-kit "2.2.0"]
                           [ring/ring-json "0.4.0"]
                           [compojure "1.6.0"]
                           [org.slf4j/slf4j-api "1.7.25"]
                           [ch.qos.logback/logback-classic "1.2.3"]
                           [gorilla-renderable "2.0.0"]
                           [gorilla-plot "0.1.4"]
                           [javax.servlet/servlet-api "2.5"]
                           [grimradical/clj-semver "0.3.0" :exclusions [org.clojure/clojure]]
                           [cider/cider-nrepl "0.16.0"]
                           [org.clojure/tools.nrepl "0.2.13"]
                           ]
  :source-paths ["src"]
  :resource-paths ["resources"]

  :main ^:skip-aot gorilla-repl.core
  :profiles {:uberjar {:aot [gorilla-repl.core gufovolante.core]}}
  :target-path "target/%s"
  ;; :plugins [[lein-gorilla "0.3.6"]]
  ;; :profiles {:uberjar {:aot :all}}
  )
