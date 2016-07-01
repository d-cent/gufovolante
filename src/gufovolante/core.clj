(ns gufovolante.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [clojure.string :as string]
            [clojure.walk :refer :all]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure-csv.core :refer :all]
            [semantic-csv.core :refer :all]
            [gorilla-repl.table :refer :all]
            [clojure.contrib.humanize :refer :all]
            [huri.core :as huri]
            )
  (:import (org.apache.commons.compress.compressors.xz XZCompressorInputStream))
  )

;; cache dei file anagrafe aperti
(def anagrafe (atom {}))

(defn apri-csv-xz
  "apri un file singolo compresso con xz, restituisce io/reader"
  [filename]
  (let [chiave (keyword filename)]

    (if (contains? @anagrafe chiave)
      ;; se gia' aperto ritorna cache
      (chiave @anagrafe)

      ;; apri nuovo file non trovato in cache
      (let []
        (swap! anagrafe assoc chiave
               (with-open [in-file (-> filename
                                       io/file
                                       io/input-stream
                                       (XZCompressorInputStream. true)
                                       io/reader)]
                 (doall (csv/read-csv in-file))))
        (chiave @anagrafe))
      )

    ))

(defn dammi-codice-uscita
  "prende una stringa del codice uscita siope e ritorna una stringa che lo descrive"
  ([codice] ;; default ritorna :descrizione
   (dammi-codice-uscita codice :descrizione))

  ([codice chiave]
  (->> (apri-csv-xz "assets/ANAG_CODGEST_USCITE.D160624.H0102.csv.xz")
       (keep #(if (string/includes? (str %) codice) %))
       (into [["codice" "categoria" "descrizione" "creazione" "scadenza"]])
       mappify
       (keep #(if (= (:codice %) codice) %))
       first
       chiave
       )))

(defn analizza-dati [dati]
  (let [colonne [:2016 :2015 :2014 :siope :desc]
        ;; prende gli importi numerici, interpreta la stringa in un intero e divide per centesimi
        rilievo (huri/derive-cols {:2016     [#(if (nil? %) 0 (quot (read-string %) 100)) :importo_2016]
                                   :2015     [#(if (nil? %) 0 (quot (read-string %) 100)) :importo_2015]
                                   :2014     [#(if (nil? %) 0 (quot (read-string %) 100)) :importo_2014]
                                   :siope    [read-string :codice_siope]
                                ;; :uscita   [#(if (nil? %) 0 (/ (read-string %) 100)) :imp_uscite_att]
                                   :desc     :descrizione_codice
                                   } dati)]
    (huri/select-cols colonne rilievo))
  )

(defn visualizza-tavola [dati]
  (table-view (map vals dati)
              :columns (some keys dati))
  )

(defn leggibile
  "prende un numero e restituisce una stringa leggibile"
  [num]
  (cond
    (nil? num) "zero"
    (< num 1) "zero"
    :else (intword num))
  )

(defn ordina-analisi
  "ordina il risultato di (analizza-dati) per [chiave] e rende le cifre piu' leggibili"
 [chiave rilievo]
  (reverse ;; assoc rende l'hashmap discendente
   (map #(assoc %
                ;; aggiunge una stringa leggibile alle cifre
               :2016 (let [v (:2016 %)] [v (leggibile v)])
               :2015 (let [v (:2015 %)] [v (leggibile v)])
               :2014 (let [v (:2014 %)] [v (leggibile v)])
               ) (sort-by (first (keys chiave))
                          (huri/where chiave rilievo)))
   )
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

(defn cerca-enti

  ;; 1 cod_ente siope
  ;; 2 data_inc_siope date,
  ;; 3 data_esc_siope date,
  ;; 4 cod_fiscale character(16),
  ;; 5 descr_ente character varying,
  ;; 6 cod_comune character(3),
  ;; 7 cod_provincia character(3),
  ;; 8 num_abitanti character varying,
  ;; 9 sottocomparto_siope character varying,

  ([needle]
   (->> (apri-csv-xz "assets/ANAG_ENTI_SIOPE.D160624.H0102.csv.xz")
        ;; fast grep on all lines as strings to exclude bulk non matching
        (keep #(if (string/includes? (str %) (string/upper-case needle)) %))
        (into [["codice" "creazione" "scadenza" "fiscale" "nome" "comune" "provincia" "popolazione" "comparto"]])
        mappify ;; this takes time
        ))

  ([needle pos]
   (->> (cerca-enti needle)
        (keep #(if (= (pos %) needle) %))
        ))

)

(defn raccogli-tutto
  "Raccoglie tutti i dati disponibili su qualsiasi ente la cui descrizione contiene la stringa"
  [stringa]
  (let [enti (cerca-enti stringa)
        tutto []]
    (map #(into tutto
                (analizza-dati (raccogli-dati "PRO" (:codice %) (:nome %))) enti))
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
