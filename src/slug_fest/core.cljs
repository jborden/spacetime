(ns slug-fest.core
  (:require [three]
            [fullscreen]
            [window-resize]
            [detector]
            [stats]
            [dat]
            ))

;; global variables
(def request-id)

(def initial-time (atom nil)) ; initial-time game loop was called

(defn x-max
  "Return the x-max that will keep on object on ground given the object and ground"
  [object ground]
  (- (/ (.-geometry.parameters.width ground) 2)
     (/ (.-geometry.parameters.width object) 2)))

(defn x-min
  "Return the x-min that will keep on object on ground given the object and ground"
  [object ground]
  (- (/ (.-geometry.parameters.width object) 2)
     (/ (.-geometry.parameters.width ground) 2)))

(defn y-max
  "Return the y-max that will keep on object on ground given the geometries of object and ground"
  [object ground]
  (- (/ (.-geometry.parameters.height ground) 2)
     (/ (.-geometry.parameters.height object) 2)))

(defn y-min
  "Return the y-min that will keep on object on ground given the object and ground"
  [object ground]
  (- (/ (.-geometry.parameters.height object) 2)
     (/ (.-geometry.parameters.height ground) 2)))


(defn mesh-box
  "Return a box that is the same size of an objects's mesh and centered on that object"
  [object]
  (let [box (js/THREE.Box2. (js/THREE.Vector2. 0 1) (js/THREE.Vector2. 1 0))]
                     (.setFromCenterAndSize box
                                            (js/THREE.Vector2. (.-mesh.position.x object)
                                                               (.-mesh.position.y object))
                                            (js/THREE.Vector2. (.-mesh.geometry.parameters.width object)
                                                               (.-mesh.geometry.parameters.height object)))
                     box))

(defn find-nearest-object
  "Find the closest object to target in object-pool"
  [target object-pool nearest-object]
  (cond
    (empty? object-pool) ;; if the object-pool was completely consumed, nearest-object is it
    nearest-object
    (nil? nearest-object) ;; if there isn't a nearest-object, initialize it's value with the first shroom of the shroom pool
    (find-nearest-object target (rest object-pool) (first object-pool))
    :else ;; otherwise, we need to do some searching
    (let [nearest-distance (.mesh.position.distanceTo target (.-mesh.position nearest-object))
          next-object      (first object-pool) ;; need to see if shroom pool is just a shroom
          next-distance    (.mesh.position.distanceTo target (.-mesh.position next-object))]
      (if (< nearest-distance next-distance) ;; the nearest shroom is still closer
          (find-nearest-object target (rest object-pool) nearest-object) ;; keep searching
          (find-nearest-object target (rest object-pool) next-object)    ;; the next shroom was closer
          ))))

(deftype Text [texture geometry material mesh]
  )

(def game-over-text
  (let [texture (js/THREE.ImageUtils.loadTexture. "resources/images/gameover.gif")
        geometry (js/THREE.PlaneGeometry. 1000 219 1 1)
        material (js/THREE.MeshBasicMaterial. (js-obj "map" texture "side" js/THREE.DoubleSide "transparent" true))
        mesh     (js/THREE.Mesh. geometry material)]
    (set! (.-position.z mesh) 10)
    (set! (.-opacity material) 0)
    (Text. texture geometry material mesh)))

(def you-win-text
  (let [texture (js/THREE.ImageUtils.loadTexture. "resources/images/youwin.gif")
        geometry (js/THREE.PlaneGeometry. 1000 219 1 1)
        material (js/THREE.MeshBasicMaterial. (js-obj "map" texture "side" js/THREE.DoubleSide "transparent" true))
        mesh     (js/THREE.Mesh. geometry material)]
    (set! (.-position.z mesh) 10)
    (set! (.-opacity material) 0)
    (Text. texture geometry material mesh)))

(deftype Ground [texture geometry material mesh]
  )

(def ground
  (let [texture  (js/THREE.ImageUtils.loadTexture. "resources/images/ground.png")
        geometry (js/THREE.PlaneGeometry. 1000 1000 10 10)
        material (js/THREE.MeshBasicMaterial. (js-obj "map" texture "side" js/THREE.DoubleSide))
        mesh     (js/THREE.Mesh. geometry material)]
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    (.repeat.set texture 10 10)
    (Ground. texture geometry material mesh)))

(deftype Hero [texture horizontal-frames vertical-frames material geometry mesh current-frame current-frame-total-display-time frame-duration direction move-increment salting? current-salt-frame salt-frame-display-time salt-frame-duration shaker-mesh shaker-box]
  Object
  
  (update-shaker-box [this]
    (let [x-offset 65
          y-offset 50
          width    45
          height   90]
      (if (= direction "right")
        (do
          (.setFromCenterAndSize shaker-box
                                 (js/THREE.Vector2. (+ (.-mesh.position.x this) x-offset) (- (.-mesh.position.y this) y-offset))
                                 (js/THREE.Vector2. width height))
          (.position.set shaker-mesh (+ (.-mesh.position.x this) x-offset) (- (.-mesh.position.y this) y-offset) (.-mesh.position.z this)))
        (do
          (.setFromCenterAndSize shaker-box
                                 (js/THREE.Vector2. (- (.-mesh.position.x this) x-offset) (- (.-mesh.position.y this) y-offset))
                                 (js/THREE.Vector2. width height))
          (.position.set shaker-mesh (- (.-mesh.position.x this) x-offset) (- (.-mesh.position.y this) y-offset) (.-mesh.position.z this))) 
        )))
  ;; move left
  (move-left  [this ground]
    (let [new-position (- (.-position.x mesh) move-increment)
          padding -70
          x-min    (+ (x-min this ground) padding)
          ]
      (if (< new-position x-min)
        (set! (.-position.x mesh) x-min)
        (.translateX mesh move-increment))
      ;; rotate position of salt-shaker
      (if (= direction "right")
        (do (.rotateY mesh Math.PI)
            (set! (.-direction this) "left")))
      ))
  ;; move up
  (move-up    [this ground]
    (let [new-position (+ (.-position.y mesh) move-increment)
          padding 150
          y-max   (+ (y-max this ground) padding)
          ]
      (if (> new-position y-max)
        (set! (.-position.y mesh) y-max)
        (.translateY mesh move-increment))))
  ;; move right
  (move-right [this ground]
    (let [new-position (+ (.-position.x mesh) move-increment)
          padding 70
          x-max    (+ (x-max this ground) padding)
          ]
      (if (> new-position x-max)
        (set! (.-position.x mesh) x-max)
        (.translateX mesh move-increment))
      ;; rotate position of salt-shaker
      (if (= direction "left")
        (do (.rotateY mesh Math.PI)
            (set! (.-direction this) "right")))
      ))
  ;; move-down
  (move-down  [this ground]
    (let [new-position (- (.-position.y mesh) move-increment)
          ;; y-min (- (/ (.-parameters.height geometry) 2)
          ;;          (/ ground-height 2))
          y-min    (y-min this ground)
          ]
      (if (< new-position y-min)
        (set! (.-position.y mesh) y-min)
        (.translateY mesh (* -1 move-increment)))))
  ;; increment the frame display time
  (increment-frame-display-time [this dt] (set! (.-current-frame-total-display-time this)  (+ current-frame-total-display-time dt)))
  ;; check to see if shaker box is on slug
  (shaker-box-intersects-slug? [this slug]
    (.isIntersectionBox shaker-box (mesh-box slug)))
  ;; salt action
  (salt [this slug dt]
    (set! (.-salt-frame-display-time this) (+ salt-frame-display-time dt))
    ;; check to see if hero is salting a slug
    (if (.shaker-box-intersects-slug? this slug)
      ;; salt that slug!
      (.increase-salt! slug dt))
    (if (> salt-frame-display-time salt-frame-duration)
      (let [salt-frames-width  7
            salt-frames        7
            current-frame   (if (= current-salt-frame salt-frames)
                              0
                              current-salt-frame)
            current-coloumn (mod current-frame salt-frames)
            current-row     0 ;; because the salting frames are on the firt row
            ]
        ;; we are salting, yes
        (set! (.-salting? this) true)
        ;; set the texture x offset
        (set! (.-texture.offset.x this) (/ current-coloumn salt-frames-width))
        (set! (.-texture.offset.y this) (/ current-row 1))
        ;; increment salt frame
        (set! (.-current-salt-frame this) (inc current-frame))
        ;; reset the frame total display time
        (set! (.-salt-frame-display-time this) 0)
        )))
  ;; stop the salt action
  (stop-salting [this]
    ;; salting is off
    (set! (.-salting? this) false)
    ;; reset the salt frame
    (set! (.-current-salt-frame this) 0)
    ;; the display time is zero
    (set! (.-salt-frame-display-time this) 0)
    ;; reset the position of the texture
    (set! (.-texture.offset.x this) 0)
    (set! (.-texture.offset.y this) (/ 1 2)))
  ;; increment the animation frame we are on
  (increment-animation-frame [this]
    ;; if it time to increment the frame, do it
    (if (> current-frame-total-display-time frame-duration)
      (let [current-frame (if (= current-frame (* horizontal-frames vertical-frames))
                            0
                            current-frame)
            current-column (mod current-frame horizontal-frames)
            current-row    1
            ;;(js/Math.floor (/ current-frame horizontal-frames))
            ]
        ;; set the texture x offset
        (set! (-> texture .-offset .-x) (/ current-column horizontal-frames))
        ;; set the texture y offset
        (set! (-> texture .-offset .-y) (/ current-row vertical-frames))
        ;; increment current-frame
        (set! (.-current-frame this) (inc current-frame))
        ;; reset the frame total display time
        (set! (.-current-frame-total-display-time this) 0)
        ))))

(def hero
  (let [texture  (js/THREE.ImageUtils.loadTexture. "resources/images/gnome.gif")
        horizontal-frames 1
        vertical-frames   2
        material (js/THREE.MeshBasicMaterial. (js-obj "map" texture "side" js/THREE.DoubleSide "transparent" true))
        geometry (js/THREE.PlaneGeometry. 200 200 1 1)
        mesh     (js/THREE.Mesh. geometry material)
        start-frame 0
        current-frame-total-display-time 0
        frame-duration 75
        direction "right"
        move-increment 5
        salting? false
        current-salt-frame 0
        salt-frame-display-time 0
        salt-frame-duration 75
        shaker-geometry (js/THREE.BoxGeometry. 45 90 1)
        shaker-material (js/THREE.MeshBasicMaterial. 0xff0000)
        shaker-mesh     (js/THREE.Mesh. shaker-geometry shaker-material)
        shaker-box      (js/THREE.Box2. (js/THREE.Vector2. 0 1) (js/THREE.Vector2. 1 0))
        ]
    ;; modify the hero's texture, creating a wrapped texture
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    ;; the repeat is set based upon a fraction of the amount of horizontal and vertical tiles
    (.repeat.set texture
                 (/ 1 7) ; there are 7 horizontal tiles
                 (/ 1 2) ; there are only 2 layers of vertical tiles
                 )
    ;; modify the mesh position
    (.position.set mesh 0 0 1)
    (.position.set shaker-mesh 0 0 1)
    (Hero. texture horizontal-frames vertical-frames material geometry mesh start-frame current-frame-total-display-time frame-duration direction move-increment salting? current-salt-frame salt-frame-display-time salt-frame-duration shaker-mesh shaker-box)))

(deftype Shroom [texture geometry material mesh bite-time max-bite-time dead?]
  Object
  (increase-bites! [this dt]
    (do
      ;; increate the amount of salt
      (set! (.-bite-time this) (+ bite-time dt))
      ;; fade the slug out
      (set! (.-opacity material) (+  (- 1 (/ bite-time max-bite-time)) 0.5))
      (if (>= bite-time max-bite-time)
        (do ;; send dead slugs to heaven i.e. off the ground
          (set! (.-position.x mesh) 1000)
          ;; make them clear
          (set! (.-opacity material) 0)
          (set! (.-dead? this) true)))))
  )

(defn shroom []
  (let [texture  (js/THREE.ImageUtils.loadTexture. "resources/images/mushroom.png")
        geometry (js/THREE.PlaneGeometry. 100 100 1 1)
        material (js/THREE.MeshBasicMaterial. (js-obj "map" texture "side" js/THREE.DoubleSide "transparent" true))
        mesh     (js/THREE.Mesh. geometry material)
        bite-time 0
        max-bite-time 2000
        dead?     false]
    (Shroom. texture geometry material mesh bite-time max-bite-time dead?)))


(defn reset-shroom-pool!
  "Reset the shroom pool"
  [shroom-pool]
  (let [first-shroom (nth shroom-pool 0)
        second-shroom (nth shroom-pool 1)
        third-shroom  (nth shroom-pool 2)
        fourth-shroom (nth shroom-pool 3)
        fifth-shroom (nth shroom-pool 4)]
    ;; first shroom
    (set! (.-mesh.position.x first-shroom) (+ (x-min first-shroom ground) 300))
    (set! (.-mesh.position.y first-shroom) (- (y-max first-shroom ground) 300))
    ;; second shroom
    (set! (.-mesh.position.x second-shroom) (- (x-max second-shroom ground) 300))
    (set! (.-mesh.position.y second-shroom) (- (y-max second-shroom ground) 300))
    ;; third shroom
    (set! (.-mesh.position.x third-shroom) (+ (x-min third-shroom ground) 300))
    (set! (.-mesh.position.y third-shroom) (+ (y-min third-shroom ground) 300))
    ;; fourth shroom
    (set! (.-mesh.position.x fourth-shroom) (- (x-max fourth-shroom ground) 300))
    (set! (.-mesh.position.y fourth-shroom) (+ (y-min fourth-shroom ground) 300))
    ;; fifth shroom
    (set! (.-mesh.position.x fifth-shroom) 0)
    (set! (.-mesh.position.y fifth-shroom) 0)
    (doall (map (fn [shroom] (do
                               (set! (.-material.opacity shroom) 1)
                               (set! (.-dead? shroom) false)
                               (set! (.-bite-time shroom) 0)
                               )) shroom-pool))
    shroom-pool))

(defn create-shroom-pool
  "Create a shroom pool"
  []
  (let [pool (repeatedly 5 #(shroom))
        ]
    (reset-shroom-pool! pool)
    ))

(def shroom-pool)

(deftype Slug [texture geometry material mesh salt-time max-salt-time dead?]
  Object
  (increase-salt! [this dt]
    (do
      ;; increate the amount of salt
      (set! (.-salt-time this) (+ salt-time dt))
      ;; fade the slug out
      (set! (.-opacity material) (+  (- 1 (/ salt-time max-salt-time)) 0.5))
      (if (>= salt-time max-salt-time)
        (do ;; send dead slugs to heaven i.e. off the ground
          (set! (.-position.x mesh) 1000)
          ;; make them clear
          (set! (.-opacity material) 0)
          ;; set the slug as dead
          (set! (.-dead? this) true)
          ))))
  
  ;; find the nearest shrooms
  ;; note: for recursive functions, remember that the parameter "this" is a psuedo parameter
  ;; that is not really part of the function call, rather it renames the 'this' variable
  (find-nearest-shroom [this shroom-pool]
    (find-nearest-object this shroom-pool))
  ;; move towards the nearest shroom
  (seek-nearest-shroom [this shroom-pool]
    (let [nearest-shroom (.find-nearest-shroom this shroom-pool)
          slug-x   (.-mesh.position.x this)
          shroom-x (.-mesh.position.x nearest-shroom)
          slug-y        (.-mesh.position.y this)
          shroom-y      (.-mesh.position.y nearest-shroom)
          change-coord  (fn [c1 c2 dc]
                          (let [abs (fn [n] (if (pos? n) n (* -1 n) ))]
                            (cond (<= (abs (- c1 c2)) dc) ;; the position difference is less then the 
                                  c2
                                  (> c1 c2) ;; c1 is greater then
                                  (- c1 dc)
                                  (< c1 c2) ;; c2 is less then
                                  (+ c1 dc)
                                  )))
          dc   1.4
          ]
      ;; change the coordinates to get to the nearest shroom
      (set! (.-mesh.position.x this) (change-coord slug-x shroom-x dc))
      (set! (.-mesh.position.y this) (change-coord slug-y shroom-y dc))
      ))
  ;; eat (the nearest shroom)
  (eat [this shroom-pool dt]
    (let [slug-box   (mesh-box this)
          nearest-shroom (.find-nearest-shroom this shroom-pool)
          shroom-box (mesh-box nearest-shroom)]
      (if (.isIntersectionBox slug-box shroom-box) ;; the slug is near enough to eat
        ;; bite that shroom
        (.increase-bites! nearest-shroom dt)
        )))
  )


;; create a slug object
(defn slug []
  (let [texture (js/THREE.ImageUtils.loadTexture. "resources/images/slug.png")
        horizontal-frames 1
        vertical-frames   1
        material (js/THREE.MeshBasicMaterial. (js-obj "map" texture "side" js/THREE.DoubleSide "transparent" true))
        geometry (js/THREE.PlaneGeometry. 150 70 1 1)
        mesh     (js/THREE.Mesh. geometry material)
        salt-time 0
        max-salt-time 1000 ;; maximum amount of time, in milliseconds required to kill the slug
        dead?  false
        ]
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    ;; the repeat is set based upon a fraction of the amount of horizontal and vertical tiles
    (.repeat.set texture
                 (/ 1 10) ; there are 10 horizontal tiles
                 (/ 1 1) ; there are only 1 layer of vertical tiles
                 )
    (set! (.-offset.x texture) 0)
    (set! (.-offset.y texture) 0)
    (Slug. texture geometry material mesh salt-time max-salt-time dead?)))

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
        fifth-slug (nth pool 4)
        ]
    (reset-slug-pool! pool)
    ))

(def slug-pool)

(def key-state (js-obj)) ; the state of keys

;; e.keyCode in javascript
;; arrow keys
(def left-arrow 37)
(def up-arrow 38) 
(def right-arrow 39)
(def down-arrow 40)
;; wasd keys
(def a-key 65)
(def w-key 87)
(def d-key 68)
(def s-key 83)
;; space key
(def space-key 32)

(defn game-key-down!
  "Handle event related to when a user presses down on a key. This modifies key-state"
  [event]
  (aset key-state (or (.-keycode event)
                      (.-which event)) true))

(defn game-key-up!
  "Handle event related to when a user releases a key. This modifies key-state"
  [event]
  (aset key-state (or (.-keycode event)
                      (.-which event)) false))


(def scene (js/THREE.Scene.))

(defn create-scene
  "Create a THREE.Scene object.
see:http://threejs.org/docs/#Reference/Scenes/Scene"
  []
  (js/THREE.Scene.))

(def camera (let [camera (js/THREE.PerspectiveCamera.
                           45
                           (/ (.-innerWidth js/window) (.-innerHeight js/window))
                           0.1
                           20000)]
               (.add scene camera)
               (.position.set camera 0 0 1300)
               ;;(.rotateX camera 100)
               (.lookAt camera (.-position scene))
               camera))


(defn create-perspective-camera
  "Create a THREE.PerspectiveCamera with camera frustrum fov (field of view), aspect (aspect ratio),
near (near plane) and far (far plane).
see: http://threejs.org/docs/#Reference/Cameras/PerspectiveCamera"
  [fov aspect near far]
  (js/THREE.PerspectiveCamera. fov aspect near far))

(defn create-webgl-renderer
  "Create a THREE.WebGLRenderer with js-obj parameters.
Example usage: (create-webgl-renderer (js-obj \"antialias\" true))
see: http://threejs.org/docs/#Reference/Renderers/WebGLRenderer"
  [parameters]
  (js/THREE.WebGLRenderer. parameters))

(def renderer (let [renderer (if (.-webgl js/Detector)
                                (js/THREE.WebGLRenderer. (js-obj "antialias" true))
                                (js/THREE.CanvasRender.)
                                )]
                 (.setSize renderer
                           (.-innerWidth js/window)
                           (.-innerHeight js/window))
                 renderer))

(def container (let [container (-> js/document
                                    (.getElementById "ThreeJS"))]
                  (.appendChild container (.-domElement renderer))
                  container))

(def controls (js/THREE.OrbitControls. camera (.-domElement renderer)))

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

(def floor-texture (let [texture (js/THREE.ImageUtils.loadTexture. "resources/images/checkerboard.jpg")]
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


(def floor-material (js/THREE.MeshBasicMaterial. (js-obj "map" floor-texture "side" js/THREE.DoubleSide)))

(def floor-geometry (js/THREE.PlaneGeometry. 1000 1000 10 10))

(defn create-plane-geometry
  "Create a THREE.PlaneGeometry using width, height, width-segments and height-segments.
see:http://threejs.org/docs/#Reference/Extras.Geometries/PlaneGeometry"
  [width height width-segments height-segments]
  (js/THREE.PlaneGeometry. width height width-segments height-segments))


(defn create-box-geometry
  "Create a box geometry that of width, height and depth.
see: http://threejs.org/docs/#Reference/Extras.Geometries/BoxGeometry"
  [width height depth]
  (js/THREE.BoxGeometry. width height depth))


(def skybox-geometry (js/THREE.BoxGeometry. 10000 10000 10000))

(def skybox-material (js/THREE.MeshBasicMaterial. (js-obj "color" 0x7B5221 "side" js/THREE.BackSide)))

(defn create-mesh-basic-material
  "Create a THREE.MeshBasicMaterial object using the js-obj parameters.
Example usage: (create-mesh-basic-material (js-obj \"color\" 0x9999ff \"side\" js/THREE.BackSide))
see: http://threejs.org/docs/#Reference/Materials/MeshBasicMaterial"
  [parameters]
  (js/THREE.MeshBasicMaterial. parameters))

(def sphere-geometry (js/THREE.SphereGeometry. 30 32 16))

(defn create-sphere-geometry
  "Create sphere geometry of radius, number of width-segments and number of height-segments.
Returns a THREE.SphereGeometry object.
see: http://threejs.org/docs/#Reference/Extras.Geometries/SphereGeometry"
  [radius width-segments height-segments]
  (js/THREE.SphereGeometry. radius width-segments height-segments))


(def sphere-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0x000088)))

(defn create-mesh-lambert-material
  "Create a non-shiny (Lambertian) surface of hexadecimal color. Returns a THREE.Mesh object.
see: http://threejs.org/docs/#Reference/Materials/MeshLambertMaterial"
  [color]
  (js/THREE.MeshLambertMaterial. (js-obj "color" color)))

(defn create-sphere-mesh
  "Create a sphere mesh object using sphere-geometry with material with initial x,y, and z coordinates. Returns a THREE.Mesh. object"
  [sphere-geometry material x y z]
  (let [sphere (js/THREE.Mesh. sphere-geometry material)]
    (.position.set sphere x y z)
    sphere))

(def sphere (let [sphere (js/THREE.Mesh. sphere-geometry sphere-material)]
              (.position.set sphere 0 40 0)
              sphere))


(def update-controls (fn []
                       (.update controls)
                       (.update stats)))

(def render (fn [] (.render renderer scene camera)))

(defn request-animation-frame-wrapper
  "Call the function callback with previous-time"
  [callback previous-time]
  (do (js/requestAnimationFrame (fn [current-time]
                                  (callback current-time previous-time)))))

(def delay 0)

(defn game-over
  "Game is over"
  [current-time previous-time]
  (let [previous-time (if (= previous-time nil)
                        current-time
                        previous-time)
        dt  (- current-time previous-time)
        max-delay 1000
        ]
    (set! delay (+ delay dt))
    (render)
    (if (> delay max-delay)
      (if (aget key-state space-key)
        (do
          (set! slug-pool (reset-slug-pool! slug-pool))
          (set! shroom-pool (reset-shroom-pool! shroom-pool))
          (set! delay 0)
          (set! (.-mesh.position.x hero) 0)
          (set! (.-mesh.position.y hero) 150)
          (js/cancelAnimationFrame request-id)
          (set! request-id (request-animation-frame-wrapper initial-loop nil)))
        (do
          (set! request-id (request-animation-frame-wrapper game-over current-time)))
        )
      (set! request-id (request-animation-frame-wrapper game-over current-time))
      )
    )
  )

(defn initial-loop
  "Initial loop that waits for user input in order to begin the game. This loop should be callled
with request-animation-frame-wrapper so that current-time and previous-time will be given proper values. current-time is provided by requstAnimationFrame"
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
      ;; perhaps I can think of something more clever later on, but remember cond/condp won't work
      ;; because you have to account for when two keys are held simultaneously!
      ;; hero move left
      (if (or (aget key-state left-arrow)
              (aget key-state a-key))
        (.move-left hero ground)
        )
      ;; hero move up
      (if (or (aget key-state up-arrow)
              (aget key-state w-key))
        (.move-up hero ground)
        )
      ;; hero move right
      (if (or (aget key-state right-arrow)
              (aget key-state d-key))
        (.move-right hero ground)
        )
      ;; hero move down
      (if (or (aget key-state down-arrow)
              (aget key-state s-key))
        (.move-down hero ground))
      ;; update the position of the shaker box
      (.update-shaker-box hero)
      ;; salt the nearest slug
      (if (aget key-state space-key)
        (.salt hero (find-nearest-object hero slug-pool) dt)
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
        (empty? alive-shroom-pool) ;; shrooms are all dead!
        (do
          (set! (.-material.opacity game-over-text) 1)
          (js/cancelAnimationFrame request-id)
          (set! request-id (request-animation-frame-wrapper game-over nil))
          )
        (empty? alive-slug-pool) ;; slugs are all dead!
        (do
          (set! (.-material.opacity you-win-text) 1)
          (js/cancelAnimationFrame request-id)
          (set! request-id (request-animation-frame-wrapper game-over nil))
          )
        :else
        (set! request-id (request-animation-frame-wrapper initial-loop current-time))
        )))


(def floor  (let [floor-geometry (create-plane-geometry 1000 1000 10 10)
                      floor-texture  (create-tiled-texture "resources/images/checkerboard.jpg" 10 10)
                      floor-material (create-mesh-basic-material (js-obj "map" floor-texture "side" js/THREE.DoubleSide))
                      floor          (js/THREE.Mesh. floor-geometry floor-material)]
                  ;;(set! (-> floor .-position .-y) -0.5)
                  ;;(set! (-> floor .-rotation .-x) (/ Math.PI 2))
              floor))

(defn ^:export init []
  (let [ skybox (let [skybox-geometry (create-box-geometry 10000 10000 10000)
                     skybox-material (create-mesh-basic-material (js-obj "color" 0x7B5221 "side" js/THREE.BackSide))]
                  (js/THREE.Mesh. skybox-geometry skybox-material))
        ]
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
    ;;(.appendChild container (.-domElement stats))
    (.bindKey js/THREEx.FullScreen (js-obj "charCode" (.charCodeAt "m" 0)))
    (set! request-id (request-animation-frame-wrapper initial-loop nil))
    ;; add listeners for key events
    (js/addEventListener "keydown" game-key-down! true)
    (js/addEventListener "keyup"   game-key-up!   true)))



