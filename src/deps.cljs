{:foreign-libs
 [{:file "js/three75.js"
   :provides ["cljsjs.three"]}
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
   :provides ["pointer-lock-controls"]
   :requires ["cljsjs.three"]}
  {:file "js/MTLLoader.js"
   :provides ["mtl-loader"]
   :requires ["cljsjs.three"]}
  {:file "js/OBJLoader.js"
   :provides ["obj-loader"]
   :requires ["cljsjs.three"]}]
 :externs ["common/spacetime.ext.js"]}
