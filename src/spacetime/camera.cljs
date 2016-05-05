(ns spacetime.camera
  (:require [cljsjs.three]))

(defn init-camera!
  "Given a camera, initiliaze it in scene (Three.Scene object) with optional 
  [x y z] vector for initial position"
  [camera scene position]
  (.add scene camera)
  (.position.set camera 0 0 1300)
  (.lookAt camera (.-position scene))
  camera)

(defn create-perspective-camera
  "Create a THREE.PerspectiveCamera with camera frustrum fov (field of view), 
  aspect (aspect ratio), near (near plane) and far (far plane).
  see: http://threejs.org/docs/#Reference/Cameras/PerspectiveCamera"
  [fov aspect near far]
  (js/THREE.PerspectiveCamera. fov aspect near far))

;; in repl, must be called like
;; (spacetime.camera/change-position! camera [0 0 -50000])
;; outside of namespace
(defn change-position!
  "Change the position of camera by delta vector [x y z]"
  [camera delta]
  (let [x (.-position.x camera)
        y (.-position.y camera)
        z (.-position.z camera)]
    (.position.set camera
                   (+ x (nth delta 0))
                   (+ y (nth delta 1))
                   (+ z (nth delta 2)))))
