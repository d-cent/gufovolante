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
            [gorilla-repl.core :as gorilla])
  (:use     [clojure.java.shell]
            [huri core plot etl])
  (:import  [java.awt.image.BufferedImage]
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
;; carica tutti i contenuti delle colonne "codice_siope" e "importo_2015" nella variabile codici
(def codici
  (select-cols [:codice_siope :importo_2015] df))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;fxc-soldipubblici.core/codici</span>","value":"#'fxc-soldipubblici.core/codici"}
;; <=

;; @@
;; converte le tre colonne [:imp_uscite_att :codice_siope :importo_2015] in nuove colonne numeriche 
(def select
  (select-cols [:spesa :siope :uscita :descrizione_codice]
              (derive-cols {:spesa  [#(if (nil? %) 0 (read-string %)) :importo_2015]
              				:siope  [read-string :codice_siope]
              				:uscita [#(if (nil? %) 0 (read-string %)) :imp_uscite_att]} df)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;fxc-soldipubblici.core/select</span>","value":"#'fxc-soldipubblici.core/select"}
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
;; visualizza un grafico di tutte le spese ed uscite attive al di sopra di 100k eur 
(bar-chart :descrizione_codice [:spesa :uscita] {:flip? true :height 12} (where {:spesa [> 100000000]} select))
;; @@
;; =>
;;; {"type":"html","content":"<?xml version='1.0' encoding='UTF-8'?>\n<svg viewBox='0 0 648.00 864.00' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns='http://www.w3.org/2000/svg'>\n<defs>\n<style type='text/css'>\n\n    line, polyline, path, rect, circle {\n      fill: none;\n      stroke: #000000;\n      stroke-linecap: round;\n      stroke-linejoin: round;\n      stroke-miterlimit: 10.00;\n    }\n  \n</style>\n</defs>\n<rect style='stroke: none; fill: #FFFFFF;' height='100%' width='100%'/>\n<rect style='stroke-width: 1.07; stroke: #F0F0F0; fill: #F0F0F0;' height='864.00' width='648.00' y='0.00' x='0.00'/>\n<defs>\n<clipPath id='75088af9-8ced-4859-8b52-02e01a98be8a'>\n<rect height='807.86' width='210.65' y='24.47' x='361.83'/>\n</clipPath>\n</defs>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: #F0F0F0; fill: #F0F0F0;' height='807.86' width='210.65' y='24.47' x='361.83'/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,798.20 572.47,798.20 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,741.30 572.47,741.30 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,684.41 572.47,684.41 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,627.52 572.47,627.52 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,570.63 572.47,570.63 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,513.74 572.47,513.74 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,456.85 572.47,456.85 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,399.95 572.47,399.95 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,343.06 572.47,343.06 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,286.17 572.47,286.17 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,229.28 572.47,229.28 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,172.39 572.47,172.39 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,115.49 572.47,115.49 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='361.83,58.60 572.47,58.60 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='371.40,832.33 371.40,24.47 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='424.30,832.33 424.30,24.47 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='477.20,832.33 477.20,24.47 '/>\n<polyline clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 0.53; stroke: #D9D9D9; stroke-linecap: butt;' points='530.09,832.33 530.09,24.47 '/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='19.36' y='798.20' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.00' y='772.59' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='21.52' y='741.30' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.01' y='715.70' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='25.08' y='684.41' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.01' y='658.81' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='28.58' y='627.52' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.98' y='601.92' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='30.44' y='570.63' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.00' y='545.03' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='33.00' y='513.74' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.29' y='488.14' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='37.94' y='456.85' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='3.15' y='431.24' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='46.53' y='399.95' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.90' y='374.35' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='48.67' y='343.06' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='0.00' y='317.46' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='45.10' y='286.17' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='4.05' y='260.57' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='121.10' y='229.28' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='9.93' y='203.68' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='134.32' y='172.39' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='16.45' y='146.79' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='134.60' y='115.49' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='17.49' y='89.89' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #F8766D;' height='25.60' width='191.50' y='58.60' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: none; stroke-linecap: butt; fill: #00BFC4;' height='25.60' width='36.11' y='33.00' x='371.40'/>\n<rect clip-path='url(#75088af9-8ced-4859-8b52-02e01a98be8a)' style='stroke-width: 1.07; stroke: #F0F0F0;' height='807.86' width='210.65' y='24.47' x='361.83'/>\n<defs>\n<clipPath id='641cee40-31d5-4c77-9525-d77b002ff223'>\n<rect height='864.00' width='648.00' y='0.00' x='0.00'/>\n</clipPath>\n</defs>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='801.40' x='69.85'>\nInteressi passivi ad enti del settore pubblico per finanziamenti a breve\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='744.51' x='205.25'>\nUtenze e canoni per energia elettrica\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='687.62' x='227.06'>\nSpese per liti (patrocinio legale)\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='630.73' x='22.63'>\nAltre competenze ed indennità accessorie per il personale a tempo indeterminato\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='573.84' x='205.78'>\nContributi obbligatori per il personale\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='516.95' x='292.43'>\nRitenute erariali\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='460.05' x='27.34'>\nRette di ricovero in strutture per anziani/minori/handicap ed altri servizi connessi\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='403.16' x='194.39'>\nAltre spese per servizi per conto di terzi\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='346.27' x='187.13'>\nRimborso mutui e prestiti ad altri - in euro\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='289.38' x='187.14'>\nContratti di servizio per smaltimento rifiuti\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='232.49' x='121.22'>\nCompetenze fisse per il personale a tempo indeterminato\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='175.59' x='283.62'>\nAltre infrastrutture\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='118.70' x='239.01'>\nTrasferimenti correnti ad altri\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='61.81' x='265.98'>\nAltre spese per servizi\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='842.78' x='368.81'>\n0\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='842.78' x='398.35'>\n300,000,000\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='842.78' x='451.24'>\n600,000,000\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #737373; font-family: Arial;' y='842.78' x='504.14'>\n900,000,000\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 8.00pt; fill: #525252; font-family: Arial;' transform='translate(19.05,473.76) rotate(-90)'>\ndescrizione_codice\n</text>\n</g>\n<rect clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)' style='stroke-width: 1.07; stroke: none; fill: #F0F0F0;' height='47.38' width='52.85' y='404.71' x='580.98'/>\n<rect clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)' style='stroke-width: 1.07; stroke: #F0F0F0; fill: #F0F0F0;' height='17.28' width='17.28' y='413.28' x='585.23'/>\n<rect clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)' style='stroke-width: 1.07; stroke: none; stroke-linejoin: miter; fill: #F8766D;' height='15.86' width='15.86' y='413.99' x='585.94'/>\n<rect clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)' style='stroke-width: 1.07; stroke: #F0F0F0; fill: #F0F0F0;' height='17.28' width='17.28' y='430.56' x='585.23'/>\n<rect clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)' style='stroke-width: 1.07; stroke: none; stroke-linejoin: miter; fill: #00BFC4;' height='15.86' width='15.86' y='431.27' x='585.94'/>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #525252; font-family: Arial;' y='425.13' x='604.67'>\nspesa\n</text>\n</g>\n<g clip-path='url(#641cee40-31d5-4c77-9525-d77b002ff223)'>\n<text style='font-size: 7.00pt; fill: #525252; font-family: Arial;' y='442.41' x='604.67'>\nuscita\n</text>\n</g>\n</svg>\n","value":"#huri.plot.GGView{:plot-command [[:<- :g [:data.frame {:descrizione_codice [:c \"Spese per liti (patrocinio legale)\" \"Ritenute erariali\" \"Interessi passivi ad enti del settore pubblico per finanziamenti a breve\" \"Utenze e canoni per energia elettrica\" \"Altre spese per servizi\" \"Contratti di servizio per smaltimento rifiuti\" \"Rette di ricovero in strutture per anziani/minori/handicap ed altri servizi connessi\" \"Rimborso mutui e prestiti ad altri - in euro\" \"Altre infrastrutture\" \"Altre spese per servizi per conto di terzi\" \"Trasferimenti correnti ad altri\" \"Competenze fisse per il personale a tempo indeterminato\" \"Altre competenze ed indennità accessorie per il personale a tempo indeterminato\" \"Contributi obbligatori per il personale\" \"Spese per liti (patrocinio legale)\" \"Ritenute erariali\" \"Interessi passivi ad enti del settore pubblico per finanziamenti a breve\" \"Utenze e canoni per energia elettrica\" \"Altre spese per servizi\" \"Contratti di servizio per smaltimento rifiuti\" \"Rette di ricovero in strutture per anziani/minori/handicap ed altri servizi connessi\" \"Rimborso mutui e prestiti ad altri - in euro\" \"Altre infrastrutture\" \"Altre spese per servizi per conto di terzi\" \"Trasferimenti correnti ad altri\" \"Competenze fisse per il personale a tempo indeterminato\" \"Altre competenze ed indennità accessorie per il personale a tempo indeterminato\" \"Contributi obbligatori per il personale\"], :y__auto [:c 142222312 187136966 109812034 122047262 1086054366 255792714 215177348 276043668 761794842 263908730 763349129 686812095 162109089 172608788 53571 1654834 0 77939 204787861 22955524 17880285 0 93315780 5080273 99180571 56333118 5569758 2277], :series__auto [:c \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"spesa\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\" \"uscita\"]}]] [[:library :ggplot2] [:library :scales] [:library :grid] [:library :RColorBrewer] [:library :ggrepel] [:<- :palette [:brewer.pal \"Greys\" {:n 9}]] {:color.background :palette[2]} {:color.grid.major :palette[3]} {:color.axis.text :palette[6]} {:color.axis.title :palette[7]} {:color.title :palette[9]}] [:+ [:+ [:+ [:+ [:+ [:ggplot :g [:aes {:x [:reorder :descrizione_codice :y__auto], :y :y__auto, :fill :series__auto}]] [:geom_bar {:stat \"identity\", :position \"dodge\"}]] [:coord_flip]] [:scale_y_continuous {:labels :comma}]] [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:+ [:theme_bw {:base_size 9}] [:theme {:panel.background [:element_rect {:fill :color.background, :color :color.background}]}]] [:theme {:plot.background [:element_rect {:fill :color.background, :color :color.background}]}]] [:theme {:panel.border [:element_rect {:color :color.background}]}]] [:theme {:panel.grid.major [:element_line {:color :color.grid.major, :size 0.25}]}]] [:theme {:panel.grid.minor [:element_blank]}]] [:theme {:axis.ticks [:element_blank]}]] [:theme {:legend.background [:element_rect {:fill :color.background}]}]] [:theme {:legend.key [:element_rect {:fill :color.background, :color :color.background}]}]] [:theme {:legend.text [:element_text {:size 7, :color :color.axis.title}]}]] [:theme {:legend.title [:element_blank]}]] [:theme {:plot.title [:element_text {:size 10, :color :color.title, :vjust 1.25}]}]] [:theme {:axis.text.x [:element_text {:size 7, :color :color.axis.text}]}]] [:theme {:axis.text.y [:element_text {:size 7, :color :color.axis.text}]}]] [:theme {:axis.title.x [:element_text {:size 8, :color :color.axis.title, :vjust 0}]}]] [:theme {:axis.title.y [:element_text {:size 8, :color :color.axis.title, :vjust 1.25}]}]] [:theme {:plot.margin [:unit [:c 0.35 0.2 0.3 0.35] \"cm\"]}]]] [:labs {:y \"\", :title \"\", :x \"descrizione_codice\"}]]], :options {:width 9, :height 12}}"}
;; <=

;; @@

;; @@
