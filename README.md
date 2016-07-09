# Gufo Volante

![Il logo di questo software e' un gufo volante](doc/gufo-volante-kokkola.png)

Una console interattiva inspirata alla pratica del [live coding](https://en.wikipedia.org/wiki/Live_coding) che fornisce un terminale per l'analisi dei dati pubblicati dal Governo Italiano tramite progetto [SIOPE](https://www.siope.it) e [soldipubblici.gov.it](http://soldipubblici.gov.it).

[![Software by Dyne.org](https://www.dyne.org/wp-content/uploads/2015/12/software_by_dyne.png)](http://www.dyne.org)

## Requisiti (i baffi sono opzionali)

<img align="right" src="doc/Clojure-Logo.png">

Questo applicativo e' multi-piattaforma e funziona sia su GNU/Linux che su Mac/OSX che MS/Windows.

<img align="left" src="http://leiningen.org/img/leiningen.jpg">

I requisiti software da istallare sono:

 - OpenJDK http://openjdk.java.net
 - Leiningen e Clojure http://leiningen.org
 - R https://www.r-project.org
 - un browser qualsiasi che supporti JS e SVG
 
Dentro la console R per l'analisi statistica istallare i seguenti pacchetti:

```
install.packages("ggplot2")
install.packages("scales")
install.packages("grid")
install.packages("RColorBrewer")
install.packages("ggrepel")
install.packages("svglite")
```

## Utilizzo da sorgente (attenti al Gorilla!)

Una volta istallato tutto aprire un terminale nella cartella di
gufovolante e lanciare il comando: `lein run`. Dopo un po'
il comando restituira' informazioni su un indirizzo web in
locale, per esempio:

```
Gorilla-REPL: 0.3.6
Started nREPL server on port 49187
Running at http://127.0.0.1:8990/index.html .
Ctrl+C to exit.
```

A questo punto si deve aprire l'indirizzo con un browser:
`http://127.0.0.1:8990/index.html` (notare: index.html incluso)

A questo punto ci si trova nel terminale interattivo per "livecoding"
cioe' per sperimentare in tempo reale con codice e dati e
visualizzazione. Un messaggio di benvenuto fornisce un po' di link che
pero' sono solo clickabili con tasto destro

![](doc/fxc-welcome.png)

Il linguaggio usato e' Clojure che e' un dialetto del
LISP molto efficiente che offre anche varie estensioni per facilitare
la manipolazione di larghe basi dati. E' molto potente anche se ancora
poco conosciuto, in Dyne.org riusciamo a fare grandi cose con questo
linguaggio ;^)

```clojure
(ns gufovolante.term
  (:require
   [clojure.string :as string]
   [clojure.data.json :as json]
   [clojure.contrib.humanize :refer :all]
   [gufovolante.core :refer :all :reload :true])
  (:use [gorilla-repl core table latex html]
        [huri core plot etl]
        ))

;; visualizza la tavola degli enti contenenti la stringa "pescara"
(visualizza-tavola (cerca-enti "PESCARA"))
;; da qui e' possibile ricavare il codice ente

;; qui contatta soldipubblici.gov.it per raccogliere i dati desiderati
(def dati (raccogli-dati "PRO" "000016324" "Comune di Pescara"))
;; con un po' piu' di codice e' anche possibile aggregare piu' dati assieme

;; qui analizziamo i dati convertendo i valori numerici
;; questo passo e' necessario per utilizzare i dati raccolti in grafici e quant'altro
(def rilievo (analizza-dati dati))

;; qui usiamo due nuove funzioni:
;; (ordina-analisi condizione dati) usa `condizione` per ordinare ed escludere in base ad un campo
;;                                  aggiunge anche una stringa piu' leggibile agli importi
;; (visualizza-tavola rilievo) semplicemente formatta la tavola in modo da renderla piu' leggibile
(let [interessante   (ordina-analisi {:2015 [> 100000]} rilievo)]
       (visualizza-tavola interessante))
;; qui vogliamo la lista di tutti gli importi ordinati per 2015 e maggiori di 100k

;; questo stampa una bella grafichetta dei dati ricavati
(bar-chart :desc
           [:2016 :2015 :2014] {:flip? true :height 12}
           (where {:2015 [> 100000]}
                  (rest rilievo)))
```

Il codice iniziale riportato qui sopra usa `cerca-enti` per visualizzare una lista di enti che contengono la stringa "PESCARA", poi contatta il
portale soldipubblici.gov.it importando tutte le voci di spesa.
Di seguito usando i comandi `bar-chart` e `analizza-dati` e' possibile visualizzare un grafico ordinato delle voci di spesa maggiori.

Per procedere ed eseguire ogni blocco di codice premere
`shift+invio`. Tornando indietro e' posibile cambiare il codice e
rieseguirlo.

I quadri offrono esempi di manipolazione dei dati. Questo approccio e' estremamente flessibile e permette anche di salvare lo storico delle manipolazioni avvenute sui dati, permettendone quindi una verifica.

![](doc/fxc-grafico.png)

E' possibile fare molto di piu' ed in un futuro prossimo ci
ripromettiamo di continuare lo sviluppo. Contributi benvenuti
sulla pagina https://github.com/d-cent/gufovolante

Per chiedere aiuto e conoscere altri utenti e' possibile iscriversi con la propria email alla [mailinglist soldipubblici](https://mailinglists.dyne.org/cgi-bin/mailman/listinfo/soldipubblici).

## Licenza (software libero!)

Copyright © 2016 fondazione Dyne.org

Programma ideato, scritto e mantenuto da Denis Roio aka [@jaromil](https://twitter.com/jaromil) (disponibile per consulenze, corsi e collaborazioni retribuite).

GufoVolante e' realizzato utilizzando componenti di software libero sviluppati da [Rich Hickey](https://twitter.com/richhickey) (Clojure), [Jony Hudson](http://j-star.org) (Gorilla) e [Simon Belak](https://twitter.com/sbelak) (Huri).

GufoVolante non sarebbe mai esistito senza la piu' ampia ricerca su nuove tecnologie per la democrazia digitale svolta in seno al progetto [D-CENT](http://dcentproject.eu) (FP7/CAPS 610349), finanziato dall'Unione Europea nel programma per la ricerca e lo sviluppo tecnologico [CAPS](https://ec.europa.eu/programmes/horizon2020/en/h2020-section/collective-awareness-platforms-sustainability-and-social-innovation-caps) (piattaforme per la consapevolezza collettiva, per la sostenibilita' e per l'innovazione sociale).

GufoVolante e' software libero distribuito secondo la Eclipse Public License (come Clojure) in versione 1.0 o (a libera discrezione) qualsiasi versione successiva della licenza.

Il logo del gufo volante e' tratto da una foto scattata da Jani Yikangas nel paesino di Kokkola, in Finlandia.

