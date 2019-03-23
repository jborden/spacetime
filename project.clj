(defproject spacetime "0.3.0-SNAPSHOT"
  :description "ClojureScript WebGL engine"
  :url "https://github.com/jborden/spacetime"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [cljsjs/three "0.1.01-1"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :source-paths ["src"]

  :clean-targets ["out" "out-adv"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler {:main spacetime.core
                                   :output-to "out/spacetime.js"
                                   :output-dir "out"
                                   :optimizations :none
                                   :foreign-libs [{:file "src/js/THREEx.FullScreen.js"
                                                   :provides ["fullscreen"]}
                                                  {:file "src/js/THREEx.WindowResize.js"
                                                   :provides ["window-resize"]}
                                                  {:file "src/js/Detector.js"
                                                   :provides ["detector"]}
                                                  {:file "src/js/Stats.js"
                                                   :provides ["stats"]}
                                                  {:file "src/js/dat.gui.js"
                                                   :provides ["dat"]}
                                                  {:file "src/js/PointerLockControls.js"
                                                   :provides ["pointer-lock-controls"]
                                                   :requires ["cljsjs.three"]}]
                                   :pretty-print true
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src"]
                        :compiler {
                                   :main spacetime.core
                                   :output-to "out-adv/spacetime.min.js"
                                   :optimizations :advanced
                                   :foreign-libs [{:file "js/THREEx.FullScreen.js"
                                                   :provides ["fullscreen"]}
                                                  {:file "js/THREEx.WindowResize.js"
                                                   :provides ["window-resize"]}
                                                  {:file "js/Detector.js"
                                                   :provides ["detector"]}
                                                  {:file "js/Stats.js"
                                                   :provides ["stats"]}
                                                  {:file "js/dat.gui.js"
                                                   :provides ["dat"]}
                                                  {:file "js/PointerLockControls.js"
                                                   :provides ["pointer-lock-controls"]}]
                                   :pretty-print false}}]}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
