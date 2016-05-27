(ns spacetime.core
  (:require [cljsjs.three]
            [fullscreen]
            [window-resize]
            [detector]
            [stats]
            [dat]
            [pointer-lock-controls]
            [spacetime.camera :refer [create-perspective-camera init-camera!]]
            [spacetime.controls.original :as controls]
            [spacetime.utilities :refer [x-min x-max y-min y-max
                                         find-nearest-object]]))

(defn create-scene
  "Create a THREE.Scene object.
  see:http://threejs.org/docs/#Reference/Scenes/Scene"
  []
  (js/THREE.Scene.))

(defn window-resize!
  [renderer camera]
  (js/THREEx.WindowResize renderer camera))

(defn fullscreen!
  []
  (.bindKey js/THREEx.FullScreen (js-obj "charCode" (.charCodeAt "m" 0))))

;; see: https://github.com/mrdoob/three.js/blob/master/examples/misc_controls_pointerlock.html

;; (defn pointer-lock-change
;;   [controls]
;;   (fn [event]
;;     (.log js/console "pointer-lock-change")
;;     (.log js/console event)
;;     (set! (.-enabled controls) true)))

;; (defn pointer-lock-error
;;   [controls]
;;   (fn [event]
;;     (.log js/console "pointer-lock-error")
;;     (.log js/console event)))

;; this should be as easy as controls/controls-handler
;; should also use .exitPointerLock() for when it is disabled.
;; should also be shimmed for moz and webkit
;; see:
(defn pointer-lock-change-listener!
  "Call on-change when pointerlockchange event occurs. on-change
  is a fn that accepts the event"
  [document on-change]
  ;;(set! (.-enabled controls) false)
  (.addEventListener document "pointerlockchange"
                     on-change false)
  (.addEventListener document "webkitpointerlockchange"
                     on-change false)
  (.addEventListener document "mozpointerlockchange"
                     on-change false))

(defn pointer-lock-error-listener!
  "Call on-error when pointerlockerror event occurs. on-error
  is a fn that accepts the event"
  [document on-error]
  ;;(set! (.-enabled controls) false)
  (.addEventListener document "pointerlockerror"
                     on-error false)
  (.addEventListener document "webkitpointerlockerror"
                     on-error false)
  (.addEventListener document "mozpointerlockerror"
                     on-error false))


;; global variables
(def request-id (atom nil))

(defn create-webgl-renderer
  "Create a THREE.WebGLRenderer with js-obj parameters.
  Example usage: (create-webgl-renderer (js-obj \"antialias\" true))
  see: http://threejs.org/docs/#Reference/Renderers/WebGLRenderer"
  [parameters]
  (js/THREE.WebGLRenderer. parameters))

(defn create-renderer
  []
  (let [renderer
        (if (.-webgl js/Detector)
          (js/THREE.WebGLRenderer. (js-obj "antialias" true))
          (js/THREE.CanvasRender.))]
    (.setSize renderer
              (.-innerWidth js/window)
              (.-innerHeight js/window))
    renderer))

(defn render
  [renderer scene camera]
  (fn [] (.render renderer scene camera)))

(defn attach-renderer!
  "Attach renderer to container with div-id"
  [renderer container]
  (.appendChild container (.-domElement renderer)))

(defn pointer-lock-controls
  [camera]
  (js/THREE.PointerLockControls. camera))

(defn translate-position!
  "Translate the position of the object where position
  is an x y z vector"
  [obj pos]
  (.translateX obj (nth pos 0))
  (.translateY obj (nth pos 1))
  (.translateZ obj (nth pos 2)))

(defn translate-controls!
  "Translate the position of the pointerlock controls where position
  is an x y z vector"
  [controls position]
  (let [object (.getObject controls)]
    (translate-position! object position)))

(defn get-position
  "Return a vector of [x y z] if  object has property 'position' that is
  THREE.Vector3"
  [controls]
  (let [object (.getObject controls)
        position (.-position object)]
    {:x (.-x position)
     :y (.-y position)
     :z (.-z position)}))

(defn set-position!
  "Set the position with [x y z] vector"
  [controls position]
  (let [object (.getObject controls)]
    (apply  #(.position.set object %1 %2 %3) position)))

(defn three-vec
  "Create a THREE/Vector from a 3-element clojure vector coords"
  [[x y z]]
  (js/THREE.Vector3. x y z ))

;; (do
;;   ;; requestPointLock can not be called automatically. Must be called from
;;   ;; the user's feedback
;;   ;; we should develop this further to overlay and lock screen and give a message to user to click
;;   ;;
;;   ;; also, we need to stub out for the firefox fullscreen workaround
;;   ;; see: http://stackoverflow.com/questions/19854708/seems-something-is-wrong-when-trying-to-set-requestpointerlock
;;   (.addEventListener container "click"
;;                      (fn [event]
;;                        (-> js/document
;;                            .-body
;;                            ;; we should stub this out for webkit and moz
;;                            (.requestPointerLock)))))

;; shows the FPS stats
;; (def stats  (let [stats (js/Stats.)]
;;               (set! (->
;;                      stats
;;                      .-domElement
;;                      .-style
;;                      .-position)
;;                     "absolute")
;;               (set! (-> stats
;;                         .-domElement
;;                         .-style
;;                         .-bottom)
;;                     "0px")
;;               (set! (-> stats
;;                         .-domElement
;;                         .-style
;;                         .-zIndex)
;;                     100)
;;               ;; (aset stats ".domElement.style.bottom" "0px")
;;               ;; (aset stats ".domElement.style.zIndex" 100)
;;               stats))

;; (defn create-light
;;   "Create a light with hexadecimal color at coordinates x,y, and z"
;;   [color x y z]
;;   (let [light (js/THREE.PointLight. color)]
;;     (.position.set light x y z)
;;     light))

;; (defn create-tiled-texture
;;   "Create a THREE.Texture using image tiled U by V times.
;;   see: http://threejs.org/docs/#Reference/Extras/ImageUtils
;;      http://threejs.org/docs/#Reference/Textures/Texture"
;;   [image-str U V]
;;   (let [texture (js/THREE.ImageUtils.loadTexture. image-str)]
;;     (aset texture "wrapS" js/THREE.RepeatWrapping)
;;     (aset texture "wrapT" js/THREE.RepeatWrapping)
;;     (.repeat.set texture U V)
;;     texture))


;; (def floor-material (js/THREE.MeshBasicMaterial.
;;                      (js-obj "map" floor-texture "side" js/THREE.DoubleSide)))

;; (def floor-geometry (js/THREE.PlaneGeometry. 1000 1000 10 10))

;; (defn create-plane-geometry
;;   "Create a THREE.PlaneGeometry using width, height, width-segments and
;;   height-segments.
;;   see: http://threejs.org/docs/#Reference/Extras.Geometries/PlaneGeometry "
;;   [width height width-segments height-segments]
;;   (js/THREE.PlaneGeometry. width height width-segments height-segments))


(defn create-box-geometry
  "Create a box geometry that of width, height and depth.
  see: http://threejs.org/docs/#Reference/Extras.Geometries/BoxGeometry"
  [width height depth]
  (js/THREE.BoxGeometry. width height depth))


;; (def skybox-geometry (js/THREE.BoxGeometry. 10000 10000 10000))

;; (def skybox-material (js/THREE.MeshBasicMaterial.
;;                       (js-obj "color" 0x7B5221 "side" js/THREE.BackSide)))

(defn create-mesh-basic-material
  "Create a THREE.MeshBasicMaterial object using the js-obj parameters.
  Example usage: (create-mesh-basic-material
                   (js-obj \"color\" 0x9999ff \"side\" js/THREE.BackSide))
  see: http://threejs.org/docs/#Reference/Materials/MeshBasicMaterial"
  [parameters]
  (js/THREE.MeshBasicMaterial. parameters))

;; (def sphere-geometry (js/THREE.SphereGeometry. 30 32 16))

(defn create-sphere-geometry
  "Create sphere geometry of radius, number of width-segments and number of
  height-segments. Returns a THREE.SphereGeometry object.
  see: http://threejs.org/docs/#Reference/Extras.Geometries/SphereGeometry"
  [radius width-segments height-segments]
  (js/THREE.SphereGeometry. radius width-segments height-segments))


;; (def sphere-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x000088)))

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

;; (def update-controls (fn []
;;                        ;; updates the view with the mouse
;;                        ;;(.update orbit-controls)
;;                        ;; updates the FPS
;;                        ;;(.update stats)
;;                        ))

;; needs camera
;; (def render (fn [] (.render renderer scene camera)))

(defn request-animation-frame
  "Call the function callback with previous-time"
  [callback previous-time]
  (do (js/requestAnimationFrame (fn [current-time]
                                  (callback current-time previous-time)))))


(defn time-frame-loop
  "Each moment of time occurs within the time-frame-loop aka game-loop.
  The time-frame is an instance of time defined as
  delta-t (Δt) = current-time - previous-time.

  current-time and previous-time are defined by request-animation-frame.

  f is called on dt at each instance.

  delta-t is
  \"usually 60 times per second, but will generally
  match the display refresh rate in most web browsers as per W3C
  recommendation.\"
  https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame

  delta-t can be modified by a factor of chi. The default value of chi is 1.

  time-frame-loop must be initially called by request-animation-frame.

  ex: (request-animation-frame
                      (time-frame-loop
                       (fn [delta-t]
                         (do (render)
                             (controls/controls-handler camera)))) nil)"
  [f & [chi]]
  (fn [current-time previous-time]
    (let [previous-time (if (= previous-time nil)
                          current-time
                          previous-time)
          delta-t  (- current-time previous-time) ;  Δt
          chi (or chi 1) ; Χ, after Χρόνος aka chronos
          ]
      (f (* delta-t chi))
      (cond
        :else
        (set! request-id (request-animation-frame
                          (time-frame-loop f chi) current-time))))))

(defn start-time-frame-loop
  "Start time-frame-loop using f, keeping track of the request-id of
  requestAnimationFrame in the request-id atom. chi is optional.

  See time-frame-loop for a description of f and chi."
  [f request-id & [chi]]
  (reset! request-id (request-animation-frame (time-frame-loop f chi) nil)))

;; (def game-over-delay 0)

;; (defn stop-time-frame-loop
;;   "Game is over"
;;   [current-time previous-time]
;;   (let [previous-time (if (= previous-time nil)
;;                         current-time
;;                         previous-time)
;;         delta-t  (- current-time previous-time)
;;         max-delay 1000]
;;     (set! game-over-delay (+ game-over-delay delta-t))
;;     (render)
;;     (if (> game-over-delay max-delay)
;;       (if (aget controls/key-state controls/space-key)
;;         (do
;;           (set! game-over-delay 0)
;;           (js/cancelAnimationFrame request-id)
;;           (set! request-id (request-animation-frame time-frame-loop nil)))
;;         (do
;;           (set! request-id
;;                 (request-animation-frame stop-time-frame-loop current-time))))
;;       (set! request-id
;;             (request-animation-frame stop-time-frame-loop current-time)))))


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

(defn initialize-spacetime
  "Initialize spacetime environment given f"
  [{:keys [scene camera renderer container controls time-frame-loop]} env]
  ()
  )

;; (defn ^:export init []
;;   (let [scene (create-scene)
;;         camera (init-camera! (create-perspective-camera
;;                               75
;;                               (/ (.-innerWidth js/window)
;;                                  (.-innerHeight js/window))
;;                               1
;;                               1000)
;;                              scene
;;                              [0 0 1300])
;;         renderer (create-renderer)
;;         render (render renderer scene camera)
;;         container (-> js/document
;;                       (.getElementById "ThreeJS"))
;;         _ (attach-renderer! renderer container)
;;         _ (.addEventListener container "click"
;;                              (fn [event]
;;                                (-> js/document
;;                                    .-body
;;                                    ;; we should stub this out for webkit and moz
;;                                    (.requestPointerLock))))
;;         pointer-lock-controls (pointer-lock-controls camera)
;;         _ (.add scene (.getObject pointer-lock-controls))
;;         _ (pointer-lock-listener! js/document pointer-lock-controls)
;;         skybox (let [skybox-geometry (create-box-geometry 20000 20000 20000)
;;                      skybox-material (create-mesh-basic-material
;;                                       (js-obj "color" 0x063140
;;                                               "side" js/THREE.BackSide))]
;;                  (js/THREE.Mesh. skybox-geometry skybox-material))
;;         light  (js/THREE.HemisphereLight. 0xeeeeff 0x777788 0.75)
;;         red-sphere (sphere 200 1 1 1000)
;;         ]
;;     ;;(.add scene skybox)
;;     (.add scene (.-mesh red-sphere))
;;     (js/THREEx.WindowResize renderer camera)
;;     ;;(.appendChild container (.-domElement stats))
;;     (.bindKey js/THREEx.FullScreen (js-obj "charCode" (.charCodeAt "m" 0)))
;;     (start-time-frame-loop (fn [delta-t]
;;                              (do (render)
;;                                  (controls/controls-handler camera)))
;;                            request-id)
;;     ;; add listeners for key events
;;     (js/addEventListener "keydown" controls/game-key-down! true)
;;     (js/addEventListener "keyup"   controls/game-key-up!   true)))
