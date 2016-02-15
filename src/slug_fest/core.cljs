(ns slug-fest.core
  (:require [three]
            [fullscreen]
            [window-resize]
            [detector]
            [stats]
            [dat]
            [slug-fest.camera :refer [change-position!
                                      create-perspective-camera init-camera!]]
            [slug-fest.controls :as controls]
            [slug-fest.entities :refer [hero shroom slug]]
            [slug-fest.utilities :refer [x-min x-max y-min y-max
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


;; global variables
(def request-id)

(def initial-time (atom nil)) ; initial-time game loop was called

(deftype Text [texture geometry material mesh])

(def game-over-text
  (let [texture (js/THREE.ImageUtils.loadTexture.
                 "resources/images/gameover.gif")
        geometry (js/THREE.PlaneGeometry. 1000 219 1 1)
        material (js/THREE.MeshBasicMaterial.
                  (js-obj "map" texture "side" js/THREE.DoubleSide
                          "transparent" true))
        mesh     (js/THREE.Mesh. geometry material)]
    (set! (.-position.z mesh) 10)
    (set! (.-opacity material) 0)
    (Text. texture geometry material mesh)))

(def you-win-text
  (let [texture (js/THREE.ImageUtils.loadTexture. "resources/images/youwin.gif")
        geometry (js/THREE.PlaneGeometry. 1000 219 1 1)
        material (js/THREE.MeshBasicMaterial. (js-obj "map" texture "side"
                                                      js/THREE.DoubleSide
                                                      "transparent" true))
        mesh     (js/THREE.Mesh. geometry material)]
    (set! (.-position.z mesh) 10)
    (set! (.-opacity material) 0)
    (Text. texture geometry material mesh)))

(deftype Ground [texture geometry material mesh])

(def ground
  (let [texture  (js/THREE.ImageUtils.loadTexture.
                  "resources/images/ground.png")
        geometry (js/THREE.PlaneGeometry. 1000 1000 10 10)
        material (js/THREE.MeshBasicMaterial.
                  (js-obj "map" texture
                          "side" js/THREE.DoubleSide))
        mesh     (js/THREE.Mesh. geometry material)]
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    (.repeat.set texture 10 10)
    (Ground. texture geometry material mesh)))

(defn reset-shroom-pool!
  "Reset the shroom pool"
  [shroom-pool]
  (let [first-shroom (nth shroom-pool 0)
        second-shroom (nth shroom-pool 1)
        third-shroom  (nth shroom-pool 2)
        fourth-shroom (nth shroom-pool 3)
        fifth-shroom (nth shroom-pool 4)]
    ;; first shroom
    (set! (.-mesh.position.x first-shroom)
          (+ (x-min first-shroom ground) 300))
    (set! (.-mesh.position.y first-shroom)
          (- (y-max first-shroom ground) 300))
    ;; second shroom
    (set! (.-mesh.position.x second-shroom)
          (- (x-max second-shroom ground) 300))
    (set! (.-mesh.position.y second-shroom)
          (- (y-max second-shroom ground) 300))
    ;; third shroom
    (set! (.-mesh.position.x third-shroom)
          (+ (x-min third-shroom ground) 300))
    (set! (.-mesh.position.y third-shroom)
          (+ (y-min third-shroom ground) 300))
    ;; fourth shroom
    (set! (.-mesh.position.x fourth-shroom)
          (- (x-max fourth-shroom ground) 300))
    (set! (.-mesh.position.y fourth-shroom)
          (+ (y-min fourth-shroom ground) 300))
    ;; fifth shroom
    (set! (.-mesh.position.x fifth-shroom) 0)
    (set! (.-mesh.position.y fifth-shroom) 0)
    (doall (map
            (fn [shroom] (do
                           (set! (.-material.opacity shroom) 1)
                           (set! (.-dead? shroom) false)
                           (set! (.-bite-time shroom) 0)))
            shroom-pool))
    shroom-pool))

(defn create-shroom-pool
  "Create a shroom pool"
  []
  (let [pool (repeatedly 5 #(shroom))]
    (reset-shroom-pool! pool)))

(def shroom-pool)

(defn reset-slug-pool!
  "Reset the slug pool"
  [slug-pool]
  (let [first-slug (nth slug-pool 0)
        second-slug (nth slug-pool 1)
        third-slug  (nth slug-pool 2)
        fourth-slug (nth slug-pool 3)
        fifth-slug (nth slug-pool 4)]
    ;; first slug
    (set! (.-mesh.position.x first-slug) (x-min first-slug ground))
    (set! (.-mesh.position.y first-slug) (y-max first-slug ground))
    ;; second slug
    (set! (.-mesh.position.x second-slug) (x-max second-slug ground))
    (set! (.-mesh.position.y second-slug) (y-max second-slug ground))
    ;; third slug
    (set! (.-mesh.position.x third-slug) (x-min third-slug ground))
    (set! (.-mesh.position.y third-slug) (y-min third-slug ground))
    ;; fourth slug
    (set! (.-mesh.position.x fourth-slug) (x-max fourth-slug ground))
    (set! (.-mesh.position.y fourth-slug) (y-min fourth-slug ground))
    ;; fifth slug
    (set! (.-mesh.position.x fifth-slug) (+  (x-max fifth-slug ground) 100))
    (set! (.-mesh.position.y fifth-slug) (+ (y-min fifth-slug ground) 100))
    (doall (map (fn [slug] (do (set! (.-dead? slug) false)
                               (set! (.-salt-time slug) 0)
                               (set! (.-material.opacity slug) 1)
                               )) slug-pool))
    slug-pool))

(defn create-slug-pool
  "Create a slug pool"
  []
  (let [pool (repeatedly 5 #(slug))
        first-slug (nth pool 0)
        second-slug (nth pool 1)
        third-slug  (nth pool 2)
        fourth-slug (nth pool 3)
        fifth-slug (nth pool 4)]
    (reset-slug-pool! pool)))

(def slug-pool)

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

;; Rotates the view with the mouse
;;(def orbit-controls (js/THREE.OrbitControls. camera (.-domElement renderer)))

;; shows the FPS stats
(def stats  (let [stats (js/Stats.)]
              ;;(aset stats ".domElement.style.position" "absolute")
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

(def sphere (let [sphere (js/THREE.Mesh. sphere-geometry sphere-material)]
              (.position.set sphere 0 40 0)
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
        chi 0.5
        alive-shroom-pool  (remove (fn [shroom] (.-dead? shroom)) shroom-pool)
        alive-slug-pool    (remove (fn [slug] (.-dead? slug)) slug-pool)
        ]
    (render)
    (update-controls)
    ;; text should not be visible
    (set! (.-material.opacity game-over-text) 0)
    (set! (.-material.opacity you-win-text) 0)
    ;; slug should look for nearest shroom and seek it out
    ;; are there still slugs and shrooms that are alive?
    (if (and
         (not (empty? alive-shroom-pool))
         (not (empty? alive-slug-pool))
         )
      (do
        (doall (map (fn [slug]
                      ;; move the slug to the nearest shroom
                      (.seek-nearest-shroom slug alive-shroom-pool)
                      ;; slug should try to eat
                      (.eat slug alive-shroom-pool dt))
                    alive-slug-pool
                    ))))
    (controls/controls-handler camera)
    ;; update the position of the shaker box
    (.update-shaker-box hero)
    ;; salt the nearest slug
    (if (aget controls/key-state controls/space-key)
      (.salt hero (find-nearest-object hero  slug-pool (first slug-pool)) dt)
      (.stop-salting hero))
    ;; increment the amount of time the hero's texture file has been displayed
    (if (not (.-salting hero)) ;; if the hero isn't salting, step the frames
      (.increment-frame-display-time hero dt)
      (.increment-animation-frame hero))
    ;; is the shaker box intersecting with the slug?
    ;; (if (.shaker-box-intersects-slug? hero slug)
    ;;   (.log js/console "Slug is salted"))

    ;; refresh the loop
    (cond
      ;; (empty? alive-shroom-pool) ;; shrooms are all dead!
      ;; (do
      ;;   (set! (.-material.opacity game-over-text) 1)
      ;;   (js/cancelAnimationFrame request-id)
      ;;   (set! request-id (request-animation-frame-wrapper game-over nil))
      ;;   )
      ;; (empty? alive-slug-pool) ;; slugs are all dead!
      ;; (do
      ;;   (set! (.-material.opacity you-win-text) 1)
      ;;   (js/cancelAnimationFrame request-id)
      ;;   (set! request-id (request-animation-frame-wrapper game-over nil))
      ;;   )
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
          (set! slug-pool (reset-slug-pool! slug-pool))
          (set! shroom-pool (reset-shroom-pool! shroom-pool))
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


(def floor  (let [floor-geometry (create-plane-geometry 1000 1000 10 10)
                  floor-texture  (create-tiled-texture
                                  "resources/images/checkerboard.jpg" 10 10)
                  floor-material (create-mesh-basic-material
                                  (js-obj
                                   "map" floor-texture
                                   "side" js/THREE.DoubleSide))
                  floor          (js/THREE.Mesh. floor-geometry
                                                 floor-material)]
              ;;(set! (-> floor .-position .-y) -0.5)
              ;;(set! (-> floor .-rotation .-x) (/ Math.PI 2))
              floor))

(defn ^:export init []
  (let [skybox (let [skybox-geometry (create-box-geometry 10000 10000 10000)
                     skybox-material (create-mesh-basic-material
                                      (js-obj "color" 0x7B5221
                                              "side" js/THREE.BackSide))]
                 (js/THREE.Mesh. skybox-geometry skybox-material))]
    (.add scene (.-mesh ground))
    (.add scene (.-mesh game-over-text))
    (.add scene (.-mesh you-win-text))
    (.add scene skybox)
    (.add scene (.-mesh hero))
    (set! slug-pool (create-slug-pool))
    (set! shroom-pool (create-shroom-pool))
    (set! (.-mesh.position.x hero) 0)
    (set! (.-mesh.position.y hero) 150)
    (doall (map (fn [slug] (.add scene (.-mesh slug))) slug-pool))
    (doall (map (fn [shroom] (.add scene (.-mesh shroom))) shroom-pool))
    (js/THREEx.WindowResize renderer camera)
    (.appendChild container (.-domElement stats))
    (.bindKey js/THREEx.FullScreen (js-obj "charCode" (.charCodeAt "m" 0)))
    (set! request-id (request-animation-frame-wrapper initial-loop nil))
    ;; add listeners for key events
    (js/addEventListener "keydown" controls/game-key-down! true)
    (js/addEventListener "keyup"   controls/game-key-up!   true)))



(when-not (repl/alive?)
  (repl/connect "ws://127.0.0.1:9001"))
