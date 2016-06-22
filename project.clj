(defproject fxc-soldipubblici "0.1.0-SNAPSHOT"
  :description "Terminale interattivo di analisi soldipubblici.gov.it"
  :url "http://dcentproject.eu"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.2.0"]
                 [org.clojure/data.json "0.2.6"]
                 [cheshire "5.5.0"]
                 [gorilla-repl "0.3.6"]
                 [huri "0.5.0-SNAPSHOT"]
                 ]
  :main ^:skip-aot fxc-soldipubblici.core
  :target-path "target/%s"
  :plugins [[lein-gorilla "0.3.6"]]
  :profiles {:uberjar {:aot :all}}
  
)
