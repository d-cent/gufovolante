(ns fxc-soldipubblici.core

  (:require [clj-http.client :as client]

            [cheshire.core :refer :all]

            [clojure.string :as string]
            [clojure.java.io :as io]

            [hickory.select :as s]

            [clojure.java.io :as io]

            [gorilla-repl.core :as gorilla])

  
  (:use [hickory.core]
        [clojure.java.shell])

  (:import [java.awt.image.BufferedImage]
           [javax.imageio.ImageIO]
           [java.io.ByteArrayInputStream]
           [javax.swing.ImageIcon])

  )


(defn dipingi
  "Converts graph g to a temporary PNG file using GraphViz and opens it
  in the current desktop environment's default viewer for PNG files.
  Requires GraphViz's 'dot' (or a specified algorithm) to be installed in
  the shell's path. Possible algorithms include :dot, :neato, :fdp, :sfdp,
  :twopi, and :circo"
  [g & {:keys [alg] :or {alg "dot"} :as opts}]
  (let [{png :out} (sh (name alg) "-Tpng" "-s300" :in g :out-enc :bytes)]
    (javax.imageio.ImageIO/read (java.io.ByteArrayInputStream. png))))


(defn -main [& args]
  (gorilla/run-gorilla-server {:port 8990 :project "fxc-soldipubblici"})
  ;;  (gorilla/load-worksheet {:params {:worksheet-filename "image-view-test.clj"}})
  )
