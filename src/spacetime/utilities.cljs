(ns spacetime.utilities
  (:require [three]))

(defn x-max
  "Return the x-max that will keep on object on ground given the object and
  ground"
  [object ground]
  (- (/ (.-geometry.parameters.width ground) 2)
     (/ (.-geometry.parameters.width object) 2)))

(defn x-min
  "Return the x-min that will keep on object on ground given the object and 
  ground"
  [object ground]
  (- (/ (.-geometry.parameters.width object) 2)
     (/ (.-geometry.parameters.width ground) 2)))

(defn y-max
  "Return the y-max that will keep on object on ground given the geometries of
  object and ground"
  [object ground]
  (- (/ (.-geometry.parameters.height ground) 2)
     (/ (.-geometry.parameters.height object) 2)))

(defn y-min
  "Return the y-min that will keep on object on ground given the object and
  ground"
  [object ground]
  (- (/ (.-geometry.parameters.height object) 2)
     (/ (.-geometry.parameters.height ground) 2)))

(defn mesh-box
  "Return a box that is the same size of an objects's mesh and centered on that
  object"
  [object]
  (let [box (js/THREE.Box2. (js/THREE.Vector2. 0 1) (js/THREE.Vector2. 1 0))]
    (.setFromCenterAndSize box
                           (js/THREE.Vector2. (.-mesh.position.x object)
                                              (.-mesh.position.y object))
                           (js/THREE.Vector2. (.-mesh.geometry.parameters.width
                                               object)
                                              (.-mesh.geometry.parameters.height
                                               object)))
    box))

(defn find-nearest-object
  "Find the closest object to target in object-pool"
  [target object-pool nearest-object]
  (cond
    ;; if the object-pool was completely consumed,
    ;; nearest-object is it
    (empty? object-pool)
    nearest-object
    ;; if there isn't a nearest-object, initialize its
    (nil? nearest-object)
    ;; value with the first shroom of the shroom pool
    (find-nearest-object target (rest object-pool) (first object-pool))
    :else ;; otherwise, we need to do some searching
    (let [nearest-distance (.mesh.position.distanceTo
                            target (.-mesh.position nearest-object))
          ;; need to see if shroom pool
          ;; is just a shroom
          next-object      (first object-pool)
          next-distance    (.mesh.position.distanceTo
                            target (.-mesh.position next-object))]
      (if (< nearest-distance next-distance) ;; nearest shroom is still closer
        (find-nearest-object
         target
         ;; keep searching
         (rest object-pool) nearest-object)
        (find-nearest-object
         ;; the next shroom was closer
         target (rest object-pool) next-object)))))

