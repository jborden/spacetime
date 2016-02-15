(ns slug-fest.entities
  (:require [slug-fest.utilities :refer [x-min x-max y-min y-max
                                         find-nearest-object mesh-box]]))

(deftype Hero [texture horizontal-frames vertical-frames material geometry mesh
               current-frame current-frame-total-display-time frame-duration
               direction move-increment salting? current-salt-frame
               salt-frame-display-time salt-frame-duration shaker-mesh
               shaker-box]
  Object
  (update-shaker-box [this]
    (let [x-offset 65
          y-offset 50
          width    45
          height   90]
      (if (= direction "right")
        (do
          (.setFromCenterAndSize shaker-box
                                 (js/THREE.Vector2.
                                  (+ (.-mesh.position.x this) x-offset)
                                  (- (.-mesh.position.y this) y-offset))
                                 (js/THREE.Vector2. width height))
          (.position.set shaker-mesh
                         (+ (.-mesh.position.x this) x-offset)
                         (- (.-mesh.position.y this) y-offset)
                         (.-mesh.position.z this)))
        (do
          (.setFromCenterAndSize shaker-box
                                 (js/THREE.Vector2.
                                  (- (.-mesh.position.x this) x-offset)
                                  (- (.-mesh.position.y this) y-offset))
                                 (js/THREE.Vector2. width height))
          (.position.set shaker-mesh
                         (- (.-mesh.position.x this) x-offset)
                         (- (.-mesh.position.y this) y-offset)
                         (.-mesh.position.z this))))))
  ;; move left
  (move-left  [this ground]
    (let [new-position (- (.-position.x mesh) move-increment)
          padding -70
          x-min    (+ (x-min this ground) padding)]
      (if (< new-position x-min)
        (set! (.-position.x mesh) x-min)
        (.translateX mesh move-increment))
      ;; rotate position of salt-shaker
      (if (= direction "right")
        (do (.rotateY mesh Math.PI)
            (set! (.-direction this) "left")))))
  ;; move up
  (move-up    [this ground]
    (let [new-position (+ (.-position.y mesh) move-increment)
          padding 150
          y-max   (+ (y-max this ground) padding)]
      (if (> new-position y-max)
        (set! (.-position.y mesh) y-max)
        (.translateY mesh move-increment))))
  ;; move right
  (move-right [this ground]
    (let [new-position (+ (.-position.x mesh) move-increment)
          padding 70
          x-max    (+ (x-max this ground) padding)]
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
  (increment-frame-display-time
    [this dt]
    (set! (.-current-frame-total-display-time this)
          (+ current-frame-total-display-time dt)))
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
        (set! (.-salt-frame-display-time this) 0))))
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
      (let [current-frame
            (if (= current-frame (* horizontal-frames vertical-frames))
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
        (set! (.-current-frame-total-display-time this) 0)))))

(def hero
  (let [texture  (js/THREE.ImageUtils.loadTexture. "resources/images/gnome.gif")
        horizontal-frames 1
        vertical-frames   2
        material (js/THREE.MeshBasicMaterial.
                  (js-obj "map" texture "side" js/THREE.DoubleSide
                          "transparent" true))
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
        shaker-box      (js/THREE.Box2.
                         (js/THREE.Vector2. 0 1)
                         (js/THREE.Vector2. 1 0))]
    ;; modify the hero's texture, creating a wrapped texture
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    ;; the repeat is set based upon a fraction of the amount of horizontal and
    ;; vertical tiles
    (.repeat.set texture
                 (/ 1 7) ; there are 7 horizontal tiles
                 (/ 1 2) ; there are only 2 layers of vertical tiles
                 )
    ;; modify the mesh position
    (.position.set mesh 0 0 1)
    (.position.set shaker-mesh 0 0 1)
    (Hero. texture horizontal-frames vertical-frames material geometry mesh
           start-frame current-frame-total-display-time frame-duration direction
           move-increment salting? current-salt-frame salt-frame-display-time
           salt-frame-duration shaker-mesh shaker-box)))

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
          (set! (.-dead? this) true))))))

(defn shroom []
  (let [texture  (js/THREE.ImageUtils.loadTexture.
                  "resources/images/mushroom.png")
        geometry (js/THREE.PlaneGeometry. 100 100 1 1)
        material (js/THREE.MeshBasicMaterial.
                  (js-obj "map" texture "side" js/THREE.DoubleSide
                          "transparent" true))
        mesh     (js/THREE.Mesh. geometry material)
        bite-time 0
        max-bite-time 2000
        dead?     false]
    (Shroom. texture geometry material mesh bite-time max-bite-time dead?)))

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
          (set! (.-dead? this) true)))))
  
  ;; find the nearest shrooms
  ;; note: for recursive functions, remember that the parameter "this" is a
  ;; psuedo parameter that is not really part of the function call,
  ;; rather it renames the 'this' variable
  (find-nearest-shroom [this shroom-pool]
    (find-nearest-object this shroom-pool (first shroom-pool)))
  ;; move towards the nearest shroom
  (seek-nearest-shroom [this shroom-pool]
    (let [nearest-shroom (.find-nearest-shroom this shroom-pool)
          slug-x   (.-mesh.position.x this)
          shroom-x (.-mesh.position.x nearest-shroom)
          slug-y        (.-mesh.position.y this)
          shroom-y      (.-mesh.position.y nearest-shroom)
          change-coord  (fn [c1 c2 dc]
                          (let [abs (fn [n] (if (pos? n) n (* -1 n) ))]
                            (cond (<=
                                   ;; the position difference is less then the 
                                   (abs (- c1 c2)) dc)
                                  c2
                                  (> c1 c2) ;; c1 is greater then
                                  (- c1 dc)
                                  (< c1 c2) ;; c2 is less then
                                  (+ c1 dc))))
          dc   1.4]
      ;; change the coordinates to get to the nearest shroom
      (set! (.-mesh.position.x this) (change-coord slug-x shroom-x dc))
      (set! (.-mesh.position.y this) (change-coord slug-y shroom-y dc))))
  ;; eat (the nearest shroom)
  (eat [this shroom-pool dt]
    (let [slug-box   (mesh-box this)
          nearest-shroom (.find-nearest-shroom this shroom-pool)
          shroom-box (mesh-box nearest-shroom)]
      (if (.isIntersectionBox slug-box shroom-box) ;; slug is near enough to eat
        ;; bite that shroom
        (.increase-bites! nearest-shroom dt)))))


;; create a slug object
(defn slug []
  (let [texture (js/THREE.ImageUtils.loadTexture. "resources/images/slug.png")
        horizontal-frames 1
        vertical-frames   1
        material (js/THREE.MeshBasicMaterial.
                  (js-obj "map" texture "side" js/THREE.DoubleSide
                          "transparent" true))
        geometry (js/THREE.PlaneGeometry. 150 70 1 1)
        mesh     (js/THREE.Mesh. geometry material)
        salt-time 0
        max-salt-time 1000 ;; maximum amount of time, in milliseconds
        ;; required to kill the slug
        dead?  false]
    (aset texture "wrapS" js/THREE.RepeatWrapping)
    (aset texture "wrapT" js/THREE.RepeatWrapping)
    ;; the repeat is set based upon a fraction of the amount of horizontal and
    ;; vertical tiles
    (.repeat.set texture
                 (/ 1 10) ; there are 10 horizontal tiles
                 (/ 1 1) ; there are only 1 layer of vertical tiles
                 )
    (set! (.-offset.x texture) 0)
    (set! (.-offset.y texture) 0)
    (Slug. texture geometry material mesh salt-time max-salt-time dead?)))
