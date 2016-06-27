(ns fxc-soldipubblici.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [clojure.string :as string]
            [clojure.walk :refer :all]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            )
  )

(defn analizza-dati [dati]
  (let [colonne [:2016 :2015 :2014 :siope :desc]
        rilievo (derive-cols {:2016  [#(if (nil? %) 0 (read-string %)) :importo_2016]
                              :2015  [#(if (nil? %) 0 (read-string %)) :importo_2015]
                              :2014  [#(if (nil? %) 0 (read-string %)) :importo_2014]
                              :abitanti [#(if (nil? %) 0 (read-string %)) :importo_2014] 
                              :siope    [read-string :codice_siope]
                              ;; :uscita   [#(if (nil? %) 0 (read-string %)) :imp_uscite_att]
                              :desc     :descrizione_codice
                              } dati)]
    (select-cols colonne rilievo))
  )
  

(defn raccogli-dati [comparto ente chi]
  (let [cs (cookies/cookie-store)
        df (binding [clj-http.core/*cookie-store* cs]
            (client/get "http://soldipubblici.gov.it/it" {:cookie-store cs})
            (-> (client/post "http://soldipubblici.gov.it/it/ricerca"
                             {:form-params {"codicecomparto" comparto
                                            "codiceente" ente
                                            "chi" chi
                                            "cosa" "" }
                              :cookie-store cs
                              :headers {"X-Requested-With" "XMLHttpRequest"}
                              :accept :json
                              :as :json})
                :body
                :data
                ))]
    df
  ))

(defn cerca-enti [needle]
  (let [anag (with-open [in-file (io/reader "assets/ANAG_ENTI_SIOPE.D160624.H0102.csv")]
               (doall
                (csv/read-csv in-file)))]
    (keep #(if (string/includes? (str %) (string/upper-case needle)) %) anag)
  ))


(defn query-siope-anagrafe
  "TODO: scarica ed indicizza anagrafe dai file zippati di SIOPE.it"
  []
  (let [anazip
        (-> (client/get "https://www.siope.it/Siope2Web/documenti/siope2/open/last/SIOPE_ANAGRAFICHE.zip" {:as :byte-array})
            (:body)
            (io/input-stream)
            (java.util.zip.ZipInputStream.))]
    {:comuni   (.getNextEntry anazip)
     :entrate  (.getNextEntry anazip)
     :uscite   (.getNextEntry anazip)
     :comparti (.getNextEntry anazip)
     :siope    (.getNextEntry anazip)
     :regprov  (.getNextEntry anazip)}
    ))
