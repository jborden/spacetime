(defproject spacetime "0.1.0-SNAPSHOT"
  :description "Slug Fest Game for Ludum Dare 32"
  :url "http://slugfest.cagostech.com"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.cemerick/piggieback "0.2.1"]
                 [weasel "0.7.0" :exclusions [org.clojure/clojurescript]]
                 [org.clojure/clojurescript "1.7.228"]
                 ;;[cljsjs/three "0.0.72-0"]
                 ]

  :node-dependencies [[source-map-support "0.3.2"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-externs "0.1.3"]]

  :source-paths ["src" "target/classes"]

  :clean-targets ["out" "out-adv"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler {
                                   :main spacetime.core
                                   :output-to "out/spacetime.js"
                                   :output-dir "out"
                                   :optimizations :none
                                   :foreign-libs [{:file "resources/js/three75.js"
                                                   :provides ["three"]}
                                                  {:file "resources/js/THREEx.FullScreen.js"
                                                   :provides ["fullscreen"]}
                                                  {:file "resources/js/THREEx.WindowResize.js"
                                                   :provides ["window-resize"]}
                                                  {:file "resources/js/Detector.js"
                                                   :provides ["detector"]}
                                                  {:file "resources/js/Stats.js"
                                                   :provides ["stats"]}
                                                  {:file "resources/js/dat.gui.js"
                                                   :provides ["dat"]}
                                                  {:file "resources/js/PointerLockControls.js"
                                                   :provides ["pointer-lock-controls"]
                                                   :requires ["three"]}
                                                  ;; {:file "resources/js/OrbitControls.js"
                                                  ;;  :provides ["orbit-controls"]}
                                                  ]
                                   :externs ["resources/js/externs.js"]
                                   :pretty-print true
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src"]
                        :compiler {
                                   :main spacetime.core
                                   :output-to "out-adv/spacetime.min.js"
                                   :optimizations :advanced
                                   :foreign-libs [{:file "resources/js/three.js"
                                                   :provides ["three"]}
                                                  {:file "resources/js/THREEx.FullScreen.js"
                                                   :provides ["fullscreen"]}
                                                  {:file "resources/js/THREEx.WindowResize.js"
                                                   :provides ["window-resize"]}
                                                  {:file "resources/js/Detector.js"
                                                   :provides ["detector"]}
                                                  {:file "resources/js/Stats.js"
                                                   :provides ["stats"]}
                                                  {:file "resources/js/dat.gui.js"
                                                   :provides ["dat"]}
                                                  {:file "resources/js/PointerLockControls.js"
                                                   :provides ["pointer-lock-controls"]}
                                                  ;; {:file "resources/js/OrbitControls.js"
                                                  ;;  :provides ["orbit-controls"]}
                                                  ]
                                   :externs ["resources/js/externs.js"]
                                   :pretty-print false}}]}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
