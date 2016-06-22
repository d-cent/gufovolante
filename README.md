# FXC-Soldipubblici

Una console interattiva in Clojure che fornisce un terminale per
l'analisi dei dati pubblicati dal Governo Italiano su
http://soldipubblici.gov.it

## Requisiti

 - Java 
 - Leiningen (Clojure)
 - R (`apt install r-recommended`)

Dentro l'applicativo R per l'analisi statistica istallare i seguenti pacchetti:

```
install.packages("ggplot2")
install.packages("scales")
install.packages("grid")
install.packages("RColorBrewer")
install.packages("ggrepel")
install.packages("svglite")
```
 
## Usage

Start with

```
lein gorilla
```

Then open the url printed out

Load the worksheet `ws/soldipubblici.clj` using the combo `alt+g alt+l` or the menu.

Execute code snippets with `shift enter`

TODO: documentazione

## License

Copyright © 2016 fondazione Dyne.org, Amsterdam
	written and maintained by Denis Roio <jaromil@dyne.org>

Distributed under the Eclipse Public License (same as Clojure) either
version 1.0 or (at your option) any later version.
