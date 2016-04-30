(ns spacetime.core
  (:require [three]
            [fullscreen]
            [window-resize]
            [detector]
            [stats]
            [dat]
            [pointer-lock-controls]
            [spacetime.camera :refer [change-position!
                                      create-perspective-camera init-camera!]]
            [spacetime.controls.original :as controls]
            [spacetime.entities :refer [hero shroom slug]]
            [spacetime.utilities :refer [x-min x-max y-min y-max
                                         find-nearest-object]]
            [weasel.repl :as repl]))

(defn create-scene
  "Create a THREE.Scene object.
  see:http://threejs.org/docs/#Reference/Scenes/Scene"
  []
  (js/THREE.Scene.))

(def scene (create-scene))

(def camera
  (let [camera (create-perspective-camera
                45
                (/ (.-innerWidth js/window) (.-innerHeight js/window))
                0.1
                20000)]
    (init-camera! camera scene [0 0 1300])
    camera))

(def controls (js/THREE.PointerLockControls. camera))

;; see: https://github.com/mrdoob/three.js/blob/master/examples/misc_controls_pointerlock.html
(defn pointer-lock-change [event]
  (.log js/console "pointer-lock-change")
  (set! (.-enabled controls) true))

(defn pointer-lock-error [event]
  (.log js/console "pointer-lock-error")
  (.log js/console event))

(do (.add scene (.getObject controls))
    (set! (.-enabled controls) false)
    (let [document js/document]
      ;; we should stub this out for moz/webkit *lockchange and *lockerror
      (.addEventListener document "pointerlockchange" pointer-lock-change false)
      (.addEventListener document "webkitpointerlockchange" pointer-lock-change false)
      (.addEventListener document "pointerlockerror" pointer-lock-error false)))



;; global variables
(def request-id)

(def initial-time (atom nil)) ; initial-time game loop was called

(deftype Text [texture geometry material mesh])

(deftype Ground [texture geometry material mesh])

(def ground
  (let [texture  (js/THREE.ImageUtils.loadTexture.
                  "resources/images/ground.png")
        geometry (js/THREE.PlaneGeometry. 2000 2000 100 100)
        material (js/THREE.MeshBasicMaterial.
                  (js-obj "map" texture
                          "side" js/THREE.DoubleSide))
        mesh     (js/THREE.Mesh. geometry material)]
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    (.repeat.set texture 10 10)
    ;;(.rotateX geometry (/ (- js/Math.PI) 2))
    (Ground. texture geometry material mesh)))

(defn create-webgl-renderer
  "Create a THREE.WebGLRenderer with js-obj parameters.
  Example usage: (create-webgl-renderer (js-obj \"antialias\" true))
  see: http://threejs.org/docs/#Reference/Renderers/WebGLRenderer"
  [parameters]
  (js/THREE.WebGLRenderer. parameters))

(def renderer (let [renderer
                    (if (.-webgl js/Detector)
                      (js/THREE.WebGLRenderer. (js-obj "antialias" true))
                      (js/THREE.CanvasRender.))]
                (.setSize renderer
                          (.-innerWidth js/window)
                          (.-innerHeight js/window))
                renderer))

(def container (let [container (-> js/document
                                   (.getElementById "ThreeJS"))]
                 (.appendChild container (.-domElement renderer))
                 container))

(do
  ;; requestPointLock can not be called automatically. Must be called from
  ;; the user's feedback
  ;; we should develop this further to overlay and lock screen and give a message to user to click
  ;;
  ;; also, we need to stub out for the firefox fullscreen workaround
  ;; see: http://stackoverflow.com/questions/19854708/seems-something-is-wrong-when-trying-to-set-requestpointerlock
  (.addEventListener container "click"
                     (fn [event]
                       (-> js/document
                           .-body
                           ;; we should stub this out for webkit and moz
                           (.requestPointerLock)))))

;; shows the FPS stats
(def stats  (let [stats (js/Stats.)]
              (set! (->
                     stats
                     .-domElement
                     .-style
                     .-position)
                    "absolute")
              (set! (-> stats
                        .-domElement
                        .-style
                        .-bottom)
                    "0px")
              (set! (-> stats
                        .-domElement
                        .-style
                        .-zIndex)
                    100)
              ;; (aset stats ".domElement.style.bottom" "0px")
              ;; (aset stats ".domElement.style.zIndex" 100)
              stats))

(defn create-light
  "Create a light with hexadecimal color at coordinates x,y, and z"
  [color x y z]
  (let [light (js/THREE.PointLight. color)]
    (.position.set light x y z)
    light))

(def floor-texture (let [texture
                         (js/THREE.ImageUtils.loadTexture.
                          "resources/images/checkerboard.jpg")]
                     (aset texture "wrapS" js/THREE.RepeatWrapping)
                     (aset texture "wrapT" js/THREE.RepeatWrapping)
                     (.repeat.set texture 10 10)
                     texture))

(defn create-tiled-texture
  "Create a THREE.Texture using image tiled U by V times.
  see: http://threejs.org/docs/#Reference/Extras/ImageUtils
     http://threejs.org/docs/#Reference/Textures/Texture"
  [image-str U V]
  (let [texture (js/THREE.ImageUtils.loadTexture. image-str)]
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    (.repeat.set texture U V)
    texture))


(def floor-material (js/THREE.MeshBasicMaterial.
                     (js-obj "map" floor-texture "side" js/THREE.DoubleSide)))

(def floor-geometry (js/THREE.PlaneGeometry. 1000 1000 10 10))

(defn create-plane-geometry
  "Create a THREE.PlaneGeometry using width, height, width-segments and
  height-segments.
  see: http://threejs.org/docs/#Reference/Extras.Geometries/PlaneGeometry "
  [width height width-segments height-segments]
  (js/THREE.PlaneGeometry. width height width-segments height-segments))


(defn create-box-geometry
  "Create a box geometry that of width, height and depth.
  see: http://threejs.org/docs/#Reference/Extras.Geometries/BoxGeometry"
  [width height depth]
  (js/THREE.BoxGeometry. width height depth))


(def skybox-geometry (js/THREE.BoxGeometry. 10000 10000 10000))

(def skybox-material (js/THREE.MeshBasicMaterial.
                      (js-obj "color" 0x7B5221 "side" js/THREE.BackSide)))

(defn create-mesh-basic-material
  "Create a THREE.MeshBasicMaterial object using the js-obj parameters.
  Example usage: (create-mesh-basic-material
                   (js-obj \"color\" 0x9999ff \"side\" js/THREE.BackSide))
  see: http://threejs.org/docs/#Reference/Materials/MeshBasicMaterial"
  [parameters]
  (js/THREE.MeshBasicMaterial. parameters))

(def sphere-geometry (js/THREE.SphereGeometry. 30 32 16))

(defn create-sphere-geometry
  "Create sphere geometry of radius, number of width-segments and number of
  height-segments. Returns a THREE.SphereGeometry object.
  see: http://threejs.org/docs/#Reference/Extras.Geometries/SphereGeometry"
  [radius width-segments height-segments]
  (js/THREE.SphereGeometry. radius width-segments height-segments))


(def sphere-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x000088)))

(defn create-mesh-lambert-material
  "Create a non-shiny (Lambertian) surface of hexadecimal color.
  Returns a THREE.Mesh object.
  see: http://threejs.org/docs/#Reference/Materials/MeshLambertMaterial"
  [color]
  (js/THREE.MeshLambertMaterial. (js-obj "color" color)))

(defn create-sphere-mesh
  "Create a sphere mesh object using sphere-geometry with material with initial
  x,y, and z coordinates. Returns a THREE.Mesh. object"
  [sphere-geometry material x y z]
  (let [sphere (js/THREE.Mesh. sphere-geometry material)]
    (.position.set sphere x y z)
    sphere))

(def update-controls (fn []
                       ;; updates the view with the mouse
                       ;;(.update orbit-controls)
                       ;; updates the FPS
                       (.update stats)))

;; needs camera
(def render (fn [] (.render renderer scene camera)))

(defn request-animation-frame-wrapper
  "Call the function callback with previous-time"
  [callback previous-time]
  (do (js/requestAnimationFrame (fn [current-time]
                                  (callback current-time previous-time)))))


(defn initial-loop
  "Initial loop that waits for user input in order to begin the game. This loop
  should be callled with request-animation-frame-wrapper so that current-time
  and previous-time will be given proper values. current-time is provided by
  requstAnimationFrame"
  [current-time previous-time]
  (let [
        previous-time (if (= previous-time nil)
                        current-time
                        previous-time)
        dt  (- current-time previous-time)
        chi 0.5]
    (render)
    (update-controls)
    (controls/controls-handler camera)
    ;; refresh the loop
    (cond
      :else
      (set! request-id (request-animation-frame-wrapper
                        initial-loop current-time)))))
(def game-over-delay 0)

(defn game-over
  "Game is over"
  [current-time previous-time]
  (let [previous-time (if (= previous-time nil)
                        current-time
                        previous-time)
        dt  (- current-time previous-time)
        max-delay 1000]
    (set! game-over-delay (+ game-over-delay dt))
    (render)
    (if (> game-over-delay max-delay)
      (if (aget controls/key-state controls/space-key)
        (do
          (set! game-over-delay 0)
          (set! (.-mesh.position.x hero) 0)
          (set! (.-mesh.position.y hero) 150)
          (js/cancelAnimationFrame request-id)
          (set! request-id (request-animation-frame-wrapper initial-loop nil)))
        (do
          (set! request-id
                (request-animation-frame-wrapper game-over current-time))))
      (set! request-id
            (request-animation-frame-wrapper game-over current-time)))))


;; An interactive sphere for the game
(deftype Sphere
    [geometry material mesh selected?]
  Object
  ;; specify the new color using hexadecial notation
  (change-color [this color]
    (.material.color.set this color)))

(defn sphere
  "Create a sphere with initial coordinates x,y,z and radius."
  [x y z radius]
  (let [geometry (js/THREE.SphereGeometry. radius 32 16)
        material (js/THREE.MeshBasicMaterial. (js-obj
                                               "color" 0xFF0000
                                               "side" js/THREE.DoubleSide))
        mesh     (js/THREE.Mesh. geometry material)
        selected? false]
    (.position.set mesh x y z)
    (Sphere. geometry material mesh selected?)))

(defn ^:export init []
  (let [skybox (let [skybox-geometry (create-box-geometry 20000 20000 20000)
                     skybox-material (create-mesh-basic-material
                                      (js-obj "color" 0x063140
                                              "side" js/THREE.BackSide))]
                 (js/THREE.Mesh. skybox-geometry skybox-material))
        light  (js/THREE.HemisphereLight. 0xeeeeff 0x777788 0.75)
        red-sphere (sphere 200 1 1 1000)]
    (.add scene skybox)
    (.add scene (.-mesh red-sphere))
    (js/THREEx.WindowResize renderer camera)
    (.appendChild container (.-domElement stats))
    (.bindKey js/THREEx.FullScreen (js-obj "charCode" (.charCodeAt "m" 0)))
    (set! request-id (request-animation-frame-wrapper initial-loop nil))
    ;; add listeners for key events
    (js/addEventListener "keydown" controls/game-key-down! true)
    (js/addEventListener "keyup"   controls/game-key-up!   true)))


;; (when-not (repl/alive?)
;;   (repl/connect "ws://127.0.0.1:9001"))
