;; gorilla-repl.fileformat = 1

;; **
;;; # FXC-Soldipubblici
;;; 
;;; ## Terminale interattivo per analisi di dati da soldipubblici.gov.it
;;; 
;;; Sviluppo in fase sperimentale, per maggiori informazioni la mailinglist pubblica e'
;;; 
;;; Archivio: https://lists.dyne.org/lurker/list/soldipubblici.it.html
;;; 
;;; Iscrizione: https://mailinglists.dyne.org/cgi-bin/mailman/listinfo/soldipubblici
;;; 
;;; Codice libero ed open source: https://github.com/dyne/fxc-soldipubblici
;;; 
;;; Questa ricerca e' legata al progetto D-CENT http://dcentproject.eu
;;; 
;;; FXC sta per http://freecoin.ch
;;; 
;;; ```
;;;   Copyright (c) Denis Roio. All rights reserved.
;;;   The use and distribution terms for this software are covered by the
;;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;   which can be found in the file epl-v10.html at the root of this distribution.
;;;   By using this software in any fashion, you are agreeing to be bound by
;;;   the terms of this license.
;;;   You must not remove this notice, or any other, from this software.
;;; ```
;; **

;; @@
(ns fxc-soldipubblici.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [cheshire.core :refer :all]
            [clojure.string :as string]
            [clojure.data.json :as json]
            [gorilla-repl.core :as gorilla]
            
)
  (:use [hickory.core]
        [clojure.java.shell]
        [huri core plot etl])
  (:import [java.awt.image.BufferedImage]
           [javax.imageio.ImageIO]
           [java.io.ByteArrayInputStream]
           [javax.swing.ImageIcon]))
(def cs (cookies/cookie-store))
(def df (binding [clj-http.core/*cookie-store* cs]




  ;; -------------------------------------------
  ;; riempire la richiesta e premere Shift+Invio
  (let [comparto "PRO"
        ente     "011135934"
        chi      "comune+di+matera"
        cosa     ""
        ]
    ;; ----------------------------





  (client/get "http://soldipubblici.gov.it/it" {:cookie-store cs})
  (-> (client/post "http://soldipubblici.gov.it/it/ricerca"
               {:form-params {"codicecomparto" comparto
                              "codiceente" ente
                              "chi" chi
                              "cosa" cosa }
               	:cookie-store cs
                :headers {"X-Requested-With" "XMLHttpRequest"}
                :accept :json
                :as :json})
      :body
      :data
  ))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;fxc-soldipubblici.core/df</span>","value":"#'fxc-soldipubblici.core/df"}
;; <=

;; @@
;; stampa tutte le colonne
(cols df)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>(:imp_uscite_att :idtable :importo_2015 :codice_gestionale :descrizione_codice :importo_2013 :codice_siope :importo_2014 :cod_ente :codice_comparto :descrizione_ente :ricerca :periodo :importo_2016 :anno)</span>","value":"(:imp_uscite_att :idtable :importo_2015 :codice_gestionale :descrizione_codice :importo_2013 :codice_siope :importo_2014 :cod_ente :codice_comparto :descrizione_ente :ricerca :periodo :importo_2016 :anno)"}
;; <=

;; @@
;; carica tutti i contenuti delle colonne "codice_siope" e "importo_2015"
(def codici
  (select-cols [:codice_siope :importo_2015] df))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;fxc-soldipubblici.core/codici</span>","value":"#'fxc-soldipubblici.core/codici"}
;; <=

;; @@
;; carica tutti i codice siope convertendoli da stringhe in numeri
(def siope
  (map #(read-string (:codice_siope %)) df))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;fxc-soldipubblici.core/siope</span>","value":"#'fxc-soldipubblici.core/siope"}
;; <=

;; @@
;; carica tutti gli importi del 2015 convertendoli in numeri
(def importi
  (map #(read-string (:importo_2015 %)) 
     (filter #(not= (:importo_2015 %) nil) 
     (select-cols [:codice_siope :importo_2015] df))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;fxc-soldipubblici.core/importi</span>","value":"#'fxc-soldipubblici.core/importi"}
;; <=

;; @@
;; TODO
(pr siope)
;; @@
;; ->
;;; (2110 2106 1321 2061 3302 2530 1206 2081 1327 1541 1808 1111 2770 1202 1626 2115 2767 2012 1105 1571 2051 1602 1209 1624 1112 1325 2801 2035 1601 1567 1334 3201 1552 1103 3412 1114 1104 1712 2761 1205 2765 2116 1340 1101 1583 2601 9996 2117 2504 2034 1335 1304 2731 3411 1803 1699 2114 1804 2032 2301 2092 1312 2531 2769 1569 1305 1313 2103 9995 1403 1701 1511 3311 1716 2711 1208 4401 1711 2112 1625 2503 2111 1307 1802 1109 1302 4601 2762 2042 2108 1531 1522 1622 1402 1404 2901 3501 1512 1121 1102 2001 1309 2113 2782 1314 1613 2763 2741 1581 2506 4701 2744 2101 2509 2701 2021 1315 1207 1714 1113 1203 2781 1621 2033 4503 1212 1131 1521 1319 1563 2792 3321 2107 1544 2093 2742 2507 1124 3324 1561 2512 2002 1333 2799 1303 3301 1332 2118 1329 1316 2802 1213 1806 2768 1523 2041 1545 4799 2766 3322 1107 4798 1611 2508 1572 2201 1134 1566 2401 1311 1612 1320 2109 2745 1326 1501 2764 2902 3502 9999 2752 1565 1551 4502 1317 1318 1543 1337 2751 2502 1306 1132 1310 1338 1570 1623 1401 1715 1324 1122 2743 2721 3402 1110 1330 1336 1106 1713 1801 4201 4301 9997 3323 2791 1542 2031 1210 1807 2511 1123 1568 1322 3325 2505 1204 1562 3401 1499 3101 1308 1323 1582 2071 2712 2722 1564 1805 4101 1211 2102 3202 9998 2011 1201 1331 1133 1115 2501)
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(pr importi)
;; @@
;; ->
;;; (23495 10872425 172608788 16924499 31135316 619043 60155755 76342363 4637 162109089 20771194 794491 13133649 686812095 763349129 36455644 15030780 3861678 5373906 2469928 35190 65607985 2756100 1835582 405129 6093068 108491 2582284 22500 2037032 8726192 1012285 84899777 18314102 1399988 11423 12139812 89400 19647339 936840 263908730 494100 756479 761794842 34200 276043668 215177348 4619053 255792714 1086054366 14010693 122047262 13415252 109812034 2935100 730975 8024350 12491957 1651324 0 22358939 707173 9517542 1755855 93000 1787012 1653200 8341631 7393684 187136966 43145882 0 50938091 732884 14529219 35685 7085896 70650143 50840953 94707947 234684 24605499 4443452 142222312 948016 1700000)
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(pr codici)
;; @@
;; ->
;;; ({:codice_siope &quot;2110&quot;, :importo_2015 nil} {:codice_siope &quot;2106&quot;, :importo_2015 &quot;23495&quot;} {:codice_siope &quot;1321&quot;, :importo_2015 nil} {:codice_siope &quot;2061&quot;, :importo_2015 nil} {:codice_siope &quot;3302&quot;, :importo_2015 nil} {:codice_siope &quot;2530&quot;, :importo_2015 nil} {:codice_siope &quot;1206&quot;, :importo_2015 nil} {:codice_siope &quot;2081&quot;, :importo_2015 nil} {:codice_siope &quot;1327&quot;, :importo_2015 &quot;10872425&quot;} {:codice_siope &quot;1541&quot;, :importo_2015 nil} {:codice_siope &quot;1808&quot;, :importo_2015 nil} {:codice_siope &quot;1111&quot;, :importo_2015 &quot;172608788&quot;} {:codice_siope &quot;2770&quot;, :importo_2015 nil} {:codice_siope &quot;1202&quot;, :importo_2015 &quot;16924499&quot;} {:codice_siope &quot;1626&quot;, :importo_2015 nil} {:codice_siope &quot;2115&quot;, :importo_2015 &quot;31135316&quot;} {:codice_siope &quot;2767&quot;, :importo_2015 nil} {:codice_siope &quot;2012&quot;, :importo_2015 nil} {:codice_siope &quot;1105&quot;, :importo_2015 nil} {:codice_siope &quot;1571&quot;, :importo_2015 nil} {:codice_siope &quot;2051&quot;, :importo_2015 nil} {:codice_siope &quot;1602&quot;, :importo_2015 nil} {:codice_siope &quot;1209&quot;, :importo_2015 nil} {:codice_siope &quot;1624&quot;, :importo_2015 nil} {:codice_siope &quot;1112&quot;, :importo_2015 &quot;619043&quot;} {:codice_siope &quot;1325&quot;, :importo_2015 &quot;60155755&quot;} {:codice_siope &quot;2801&quot;, :importo_2015 nil} {:codice_siope &quot;2035&quot;, :importo_2015 nil} {:codice_siope &quot;1601&quot;, :importo_2015 nil} {:codice_siope &quot;1567&quot;, :importo_2015 nil} {:codice_siope &quot;1334&quot;, :importo_2015 &quot;76342363&quot;} {:codice_siope &quot;3201&quot;, :importo_2015 nil} {:codice_siope &quot;1552&quot;, :importo_2015 &quot;4637&quot;} {:codice_siope &quot;1103&quot;, :importo_2015 &quot;162109089&quot;} {:codice_siope &quot;3412&quot;, :importo_2015 nil} {:codice_siope &quot;1114&quot;, :importo_2015 nil} {:codice_siope &quot;1104&quot;, :importo_2015 &quot;20771194&quot;} {:codice_siope &quot;1712&quot;, :importo_2015 nil} {:codice_siope &quot;2761&quot;, :importo_2015 nil} {:codice_siope &quot;1205&quot;, :importo_2015 &quot;794491&quot;} {:codice_siope &quot;2765&quot;, :importo_2015 nil} {:codice_siope &quot;2116&quot;, :importo_2015 &quot;13133649&quot;} {:codice_siope &quot;1340&quot;, :importo_2015 nil} {:codice_siope &quot;1101&quot;, :importo_2015 &quot;686812095&quot;} {:codice_siope &quot;1583&quot;, :importo_2015 &quot;763349129&quot;} {:codice_siope &quot;2601&quot;, :importo_2015 &quot;36455644&quot;} {:codice_siope &quot;9996&quot;, :importo_2015 nil} {:codice_siope &quot;2117&quot;, :importo_2015 &quot;15030780&quot;} {:codice_siope &quot;2504&quot;, :importo_2015 nil} {:codice_siope &quot;2034&quot;, :importo_2015 nil} {:codice_siope &quot;1335&quot;, :importo_2015 nil} {:codice_siope &quot;1304&quot;, :importo_2015 &quot;3861678&quot;} {:codice_siope &quot;2731&quot;, :importo_2015 nil} {:codice_siope &quot;3411&quot;, :importo_2015 nil} {:codice_siope &quot;1803&quot;, :importo_2015 nil} {:codice_siope &quot;1699&quot;, :importo_2015 nil} {:codice_siope &quot;2114&quot;, :importo_2015 nil} {:codice_siope &quot;1804&quot;, :importo_2015 nil} {:codice_siope &quot;2032&quot;, :importo_2015 nil} {:codice_siope &quot;2301&quot;, :importo_2015 nil} {:codice_siope &quot;2092&quot;, :importo_2015 nil} {:codice_siope &quot;1312&quot;, :importo_2015 &quot;5373906&quot;} {:codice_siope &quot;2531&quot;, :importo_2015 nil} {:codice_siope &quot;2769&quot;, :importo_2015 nil} {:codice_siope &quot;1569&quot;, :importo_2015 nil} {:codice_siope &quot;1305&quot;, :importo_2015 nil} {:codice_siope &quot;1313&quot;, :importo_2015 &quot;2469928&quot;} {:codice_siope &quot;2103&quot;, :importo_2015 &quot;35190&quot;} {:codice_siope &quot;9995&quot;, :importo_2015 nil} {:codice_siope &quot;1403&quot;, :importo_2015 nil} {:codice_siope &quot;1701&quot;, :importo_2015 &quot;65607985&quot;} {:codice_siope &quot;1511&quot;, :importo_2015 nil} {:codice_siope &quot;3311&quot;, :importo_2015 nil} {:codice_siope &quot;1716&quot;, :importo_2015 &quot;2756100&quot;} {:codice_siope &quot;2711&quot;, :importo_2015 nil} {:codice_siope &quot;1208&quot;, :importo_2015 nil} {:codice_siope &quot;4401&quot;, :importo_2015 &quot;1835582&quot;} {:codice_siope &quot;1711&quot;, :importo_2015 &quot;405129&quot;} {:codice_siope &quot;2112&quot;, :importo_2015 nil} {:codice_siope &quot;1625&quot;, :importo_2015 nil} {:codice_siope &quot;2503&quot;, :importo_2015 nil} {:codice_siope &quot;2111&quot;, :importo_2015 nil} {:codice_siope &quot;1307&quot;, :importo_2015 &quot;6093068&quot;} {:codice_siope &quot;1802&quot;, :importo_2015 &quot;108491&quot;} {:codice_siope &quot;1109&quot;, :importo_2015 nil} {:codice_siope &quot;1302&quot;, :importo_2015 nil} {:codice_siope &quot;4601&quot;, :importo_2015 &quot;2582284&quot;} {:codice_siope &quot;2762&quot;, :importo_2015 nil} {:codice_siope &quot;2042&quot;, :importo_2015 nil} {:codice_siope &quot;2108&quot;, :importo_2015 &quot;22500&quot;} {:codice_siope &quot;1531&quot;, :importo_2015 nil} {:codice_siope &quot;1522&quot;, :importo_2015 nil} {:codice_siope &quot;1622&quot;, :importo_2015 nil} {:codice_siope &quot;1402&quot;, :importo_2015 &quot;2037032&quot;} {:codice_siope &quot;1404&quot;, :importo_2015 nil} {:codice_siope &quot;2901&quot;, :importo_2015 nil} {:codice_siope &quot;3501&quot;, :importo_2015 nil} {:codice_siope &quot;1512&quot;, :importo_2015 nil} {:codice_siope &quot;1121&quot;, :importo_2015 nil} {:codice_siope &quot;1102&quot;, :importo_2015 &quot;8726192&quot;} {:codice_siope &quot;2001&quot;, :importo_2015 nil} {:codice_siope &quot;1309&quot;, :importo_2015 &quot;1012285&quot;} {:codice_siope &quot;2113&quot;, :importo_2015 &quot;84899777&quot;} {:codice_siope &quot;2782&quot;, :importo_2015 nil} {:codice_siope &quot;1314&quot;, :importo_2015 &quot;18314102&quot;} {:codice_siope &quot;1613&quot;, :importo_2015 nil} {:codice_siope &quot;2763&quot;, :importo_2015 nil} {:codice_siope &quot;2741&quot;, :importo_2015 nil} {:codice_siope &quot;1581&quot;, :importo_2015 nil} {:codice_siope &quot;2506&quot;, :importo_2015 &quot;1399988&quot;} {:codice_siope &quot;4701&quot;, :importo_2015 &quot;11423&quot;} {:codice_siope &quot;2744&quot;, :importo_2015 nil} {:codice_siope &quot;2101&quot;, :importo_2015 nil} {:codice_siope &quot;2509&quot;, :importo_2015 nil} {:codice_siope &quot;2701&quot;, :importo_2015 nil} {:codice_siope &quot;2021&quot;, :importo_2015 nil} {:codice_siope &quot;1315&quot;, :importo_2015 &quot;12139812&quot;} {:codice_siope &quot;1207&quot;, :importo_2015 &quot;89400&quot;} {:codice_siope &quot;1714&quot;, :importo_2015 nil} {:codice_siope &quot;1113&quot;, :importo_2015 &quot;19647339&quot;} {:codice_siope &quot;1203&quot;, :importo_2015 &quot;936840&quot;} {:codice_siope &quot;2781&quot;, :importo_2015 nil} {:codice_siope &quot;1621&quot;, :importo_2015 nil} {:codice_siope &quot;2033&quot;, :importo_2015 nil} {:codice_siope &quot;4503&quot;, :importo_2015 &quot;263908730&quot;} {:codice_siope &quot;1212&quot;, :importo_2015 &quot;494100&quot;} {:codice_siope &quot;1131&quot;, :importo_2015 nil} {:codice_siope &quot;1521&quot;, :importo_2015 nil} {:codice_siope &quot;1319&quot;, :importo_2015 &quot;756479&quot;} {:codice_siope &quot;1563&quot;, :importo_2015 nil} {:codice_siope &quot;2792&quot;, :importo_2015 nil} {:codice_siope &quot;3321&quot;, :importo_2015 nil} {:codice_siope &quot;2107&quot;, :importo_2015 &quot;761794842&quot;} {:codice_siope &quot;1544&quot;, :importo_2015 nil} {:codice_siope &quot;2093&quot;, :importo_2015 nil} {:codice_siope &quot;2742&quot;, :importo_2015 nil} {:codice_siope &quot;2507&quot;, :importo_2015 &quot;34200&quot;} {:codice_siope &quot;1124&quot;, :importo_2015 nil} {:codice_siope &quot;3324&quot;, :importo_2015 &quot;276043668&quot;} {:codice_siope &quot;1561&quot;, :importo_2015 nil} {:codice_siope &quot;2512&quot;, :importo_2015 nil} {:codice_siope &quot;2002&quot;, :importo_2015 nil} {:codice_siope &quot;1333&quot;, :importo_2015 &quot;215177348&quot;} {:codice_siope &quot;2799&quot;, :importo_2015 &quot;4619053&quot;} {:codice_siope &quot;1303&quot;, :importo_2015 &quot;255792714&quot;} {:codice_siope &quot;3301&quot;, :importo_2015 nil} {:codice_siope &quot;1332&quot;, :importo_2015 &quot;1086054366&quot;} {:codice_siope &quot;2118&quot;, :importo_2015 nil} {:codice_siope &quot;1329&quot;, :importo_2015 &quot;14010693&quot;} {:codice_siope &quot;1316&quot;, :importo_2015 &quot;122047262&quot;} {:codice_siope &quot;2802&quot;, :importo_2015 nil} {:codice_siope &quot;1213&quot;, :importo_2015 nil} {:codice_siope &quot;1806&quot;, :importo_2015 nil} {:codice_siope &quot;2768&quot;, :importo_2015 nil} {:codice_siope &quot;1523&quot;, :importo_2015 nil} {:codice_siope &quot;2041&quot;, :importo_2015 nil} {:codice_siope &quot;1545&quot;, :importo_2015 nil} {:codice_siope &quot;4799&quot;, :importo_2015 nil} {:codice_siope &quot;2766&quot;, :importo_2015 nil} {:codice_siope &quot;3322&quot;, :importo_2015 nil} {:codice_siope &quot;1107&quot;, :importo_2015 &quot;13415252&quot;} {:codice_siope &quot;4798&quot;, :importo_2015 nil} {:codice_siope &quot;1611&quot;, :importo_2015 &quot;109812034&quot;} {:codice_siope &quot;2508&quot;, :importo_2015 nil} {:codice_siope &quot;1572&quot;, :importo_2015 nil} {:codice_siope &quot;2201&quot;, :importo_2015 nil} {:codice_siope &quot;1134&quot;, :importo_2015 nil} {:codice_siope &quot;1566&quot;, :importo_2015 nil} {:codice_siope &quot;2401&quot;, :importo_2015 nil} {:codice_siope &quot;1311&quot;, :importo_2015 &quot;2935100&quot;} {:codice_siope &quot;1612&quot;, :importo_2015 &quot;730975&quot;} {:codice_siope &quot;1320&quot;, :importo_2015 &quot;8024350&quot;} {:codice_siope &quot;2109&quot;, :importo_2015 &quot;12491957&quot;} {:codice_siope &quot;2745&quot;, :importo_2015 nil} {:codice_siope &quot;1326&quot;, :importo_2015 &quot;1651324&quot;} {:codice_siope &quot;1501&quot;, :importo_2015 nil} {:codice_siope &quot;2764&quot;, :importo_2015 nil} {:codice_siope &quot;2902&quot;, :importo_2015 nil} {:codice_siope &quot;3502&quot;, :importo_2015 nil} {:codice_siope &quot;9999&quot;, :importo_2015 &quot;0&quot;} {:codice_siope &quot;2752&quot;, :importo_2015 nil} {:codice_siope &quot;1565&quot;, :importo_2015 nil} {:codice_siope &quot;1551&quot;, :importo_2015 nil} {:codice_siope &quot;4502&quot;, :importo_2015 nil} {:codice_siope &quot;1317&quot;, :importo_2015 nil} {:codice_siope &quot;1318&quot;, :importo_2015 &quot;22358939&quot;} {:codice_siope &quot;1543&quot;, :importo_2015 nil} {:codice_siope &quot;1337&quot;, :importo_2015 &quot;707173&quot;} {:codice_siope &quot;2751&quot;, :importo_2015 nil} {:codice_siope &quot;2502&quot;, :importo_2015 &quot;9517542&quot;} {:codice_siope &quot;1306&quot;, :importo_2015 &quot;1755855&quot;} {:codice_siope &quot;1132&quot;, :importo_2015 nil} {:codice_siope &quot;1310&quot;, :importo_2015 &quot;93000&quot;} {:codice_siope &quot;1338&quot;, :importo_2015 nil} {:codice_siope &quot;1570&quot;, :importo_2015 nil} {:codice_siope &quot;1623&quot;, :importo_2015 nil} {:codice_siope &quot;1401&quot;, :importo_2015 &quot;1787012&quot;} {:codice_siope &quot;1715&quot;, :importo_2015 nil} {:codice_siope &quot;1324&quot;, :importo_2015 nil} {:codice_siope &quot;1122&quot;, :importo_2015 nil} {:codice_siope &quot;2743&quot;, :importo_2015 nil} {:codice_siope &quot;2721&quot;, :importo_2015 nil} {:codice_siope &quot;3402&quot;, :importo_2015 nil} {:codice_siope &quot;1110&quot;, :importo_2015 nil} {:codice_siope &quot;1330&quot;, :importo_2015 nil} {:codice_siope &quot;1336&quot;, :importo_2015 &quot;1653200&quot;} {:codice_siope &quot;1106&quot;, :importo_2015 &quot;8341631&quot;} {:codice_siope &quot;1713&quot;, :importo_2015 &quot;7393684&quot;} {:codice_siope &quot;1801&quot;, :importo_2015 nil} {:codice_siope &quot;4201&quot;, :importo_2015 &quot;187136966&quot;} {:codice_siope &quot;4301&quot;, :importo_2015 &quot;43145882&quot;} {:codice_siope &quot;9997&quot;, :importo_2015 &quot;0&quot;} {:codice_siope &quot;3323&quot;, :importo_2015 nil} {:codice_siope &quot;2791&quot;, :importo_2015 nil} {:codice_siope &quot;1542&quot;, :importo_2015 nil} {:codice_siope &quot;2031&quot;, :importo_2015 nil} {:codice_siope &quot;1210&quot;, :importo_2015 &quot;50938091&quot;} {:codice_siope &quot;1807&quot;, :importo_2015 nil} {:codice_siope &quot;2511&quot;, :importo_2015 &quot;732884&quot;} {:codice_siope &quot;1123&quot;, :importo_2015 nil} {:codice_siope &quot;1568&quot;, :importo_2015 nil} {:codice_siope &quot;1322&quot;, :importo_2015 &quot;14529219&quot;} {:codice_siope &quot;3325&quot;, :importo_2015 nil} {:codice_siope &quot;2505&quot;, :importo_2015 nil} {:codice_siope &quot;1204&quot;, :importo_2015 &quot;35685&quot;} {:codice_siope &quot;1562&quot;, :importo_2015 nil} {:codice_siope &quot;3401&quot;, :importo_2015 nil} {:codice_siope &quot;1499&quot;, :importo_2015 nil} {:codice_siope &quot;3101&quot;, :importo_2015 nil} {:codice_siope &quot;1308&quot;, :importo_2015 &quot;7085896&quot;} {:codice_siope &quot;1323&quot;, :importo_2015 &quot;70650143&quot;} {:codice_siope &quot;1582&quot;, :importo_2015 &quot;50840953&quot;} {:codice_siope &quot;2071&quot;, :importo_2015 nil} {:codice_siope &quot;2712&quot;, :importo_2015 nil} {:codice_siope &quot;2722&quot;, :importo_2015 nil} {:codice_siope &quot;1564&quot;, :importo_2015 nil} {:codice_siope &quot;1805&quot;, :importo_2015 nil} {:codice_siope &quot;4101&quot;, :importo_2015 &quot;94707947&quot;} {:codice_siope &quot;1211&quot;, :importo_2015 &quot;234684&quot;} {:codice_siope &quot;2102&quot;, :importo_2015 &quot;24605499&quot;} {:codice_siope &quot;3202&quot;, :importo_2015 nil} {:codice_siope &quot;9998&quot;, :importo_2015 nil} {:codice_siope &quot;2011&quot;, :importo_2015 nil} {:codice_siope &quot;1201&quot;, :importo_2015 &quot;4443452&quot;} {:codice_siope &quot;1331&quot;, :importo_2015 &quot;142222312&quot;} {:codice_siope &quot;1133&quot;, :importo_2015 &quot;948016&quot;} {:codice_siope &quot;1115&quot;, :importo_2015 nil} {:codice_siope &quot;2501&quot;, :importo_2015 &quot;1700000&quot;})
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@

;; @@
