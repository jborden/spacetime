(ns slug-fest.controls.original
  (:require [slug-fest.camera :refer [change-position!]]))

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
  "Handle event related to when a user presses down on a key. This modifies 
  key-state"
  [event]
  (aset key-state (or (.-keycode event)
                      (.-which event)) true))

(defn game-key-up!
  "Handle event related to when a user releases a key. This modifies key-state"
  [event]
  (aset key-state (or (.-keycode event)
                      (.-which event)) false))

(defn controls-handler
  "Move camera according to player controls"
  [camera]
  ;; perhaps I can think of something more clever later on, but remember
  ;; cond/condp won't work because you have to account for when two keys
  ;; are held simultaneously!

  ;; NOTE: Camera is currently floating above xy plane, these controls will have
  ;; to be adjusted when the camera is looking down the x-y plane
  
  ;; hero move left
  (if (or (aget key-state left-arrow)
          (aget key-state a-key))
    ;;(.move-left hero ground)
    (change-position! camera [-40 0 0]))
  ;; hero move up
  (if (or (aget key-state up-arrow)
          (aget key-state w-key))
    ;;(.move-up hero ground)
    (change-position! camera [0 0 -40]))
  ;; hero move right
  (if (or (aget key-state right-arrow)
          (aget key-state d-key))
    ;;(.move-right hero ground)
    (change-position! camera [40 0 0]))
  ;; hero move down
  (if (or (aget key-state down-arrow)
          (aget key-state s-key))
    ;;(.move-down hero ground)
    (do
      (change-position! camera [0 0 40]))))