(defproject spacetime "0.2.0-SNAPSHOT"
  :description "ClojureScript WebGL engine"
  :url "https://github.com/jborden/spacetime"

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
                                   :foreign-libs [{:file "src/js/three75.js"
                                                   :provides ["cljsjs.three"]}
                                                  {:file "src/js/THREEx.FullScreen.js"
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
                                                   :requires ["cljsjs.three"]}
                                                  ;; {:file "resources/js/OrbitControls.js"
                                                  ;;  :provides ["orbit-controls"]}
                                                  ]
                                   :externs ["js/spacetime_externs.js"]
                                   :pretty-print true
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src"]
                        :compiler {
                                   :main spacetime.core
                                   :output-to "out-adv/spacetime.min.js"
                                   :optimizations :advanced
                                   :foreign-libs [{:file "js/three.js"
                                                   :provides ["three"]}
                                                  {:file "js/THREEx.FullScreen.js"
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
                                                   :provides ["pointer-lock-controls"]}
                                                  ;; {:file "resources/js/OrbitControls.js"
                                                  ;;  :provides ["orbit-controls"]}
                                                  ]
                                   :externs ["js/spacetime_externs.js"]
                                   :pretty-print false}}]}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
