(ns net.mynarz.az-kviz.core
  (:require [net.mynarz.az-kviz.spec :as spec]
            [net.mynarz.az-kviz.view :refer [board]]
            [cljs.spec.alpha :as s]
            [reagent.core :as r]))

(def offset
  (partial mapv +)) 

(defn- in-board?
  "Given a board with `side` length,
  test if coordinate [x y] is in the board."
  [side [x y]]
  (and (<= x y)
       (<= 0 x side)
       (<= 0 y side)))

(def neighbours
  "Given a board with `side` length,
  return neighbours of coordinate `coords`."
  (let [possible-offsets [[-1 -1] [0 -1] [1 0] [0 1] [1 1] [-1 0]]]
    (fn [side coords]
      (->> possible-offsets
           (map (partial offset coords))
           (filter (partial in-board? side))
           (into #{})))))

(defn- sides
  "Given a board with `side` length,
  return the board sides a coordinate [x y] is at."
  [side [x y]]
  (cond-> #{}
    (zero? x) (conj :a)
    (= x y) (conj :b)
    (= y side) (conj :c)))

(defn- init-tile-state
  "Initialize state of a tile in a board with `side` length,
  The tile has `id` and is located as `coords`."
  [side id coords]
  {:coords coords
   :id id
   :neighbours (neighbours side coords)
   :sides (sides side coords)
   :status :default
   :text (-> id inc str)})

(s/fdef init-board-state
        :args (s/cat :side ::spec/side)
        :ret ::spec/board-state)
(def init-board-state
  "Initialize the state for board with `side` length."
  (letfn [(triangular-number [n]
            (/ (* n (inc n)) 2))
          (row [y length]
            (map vector (range length) (repeat y)))
          (neighbours [side [x y]])]
    (fn [side]
      (let [size (triangular-number side)
            indices (range size)
            points (->> (inc size)
                        (range 1)
                        (map-indexed row)
                        (apply concat))]
        (mapv (partial init-tile-state side)
              indices
              points)))))

; What is below is just to preview the components

(def state
  (r/atom (init-board-state 7)))

;; -------------------------
;; Views

(defn wrapper
  []
  [board {:tile-config {:on-click (fn [id]
                                    (swap! state assoc-in [id :status] :player-1))}}
         @state])

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (r/render [wrapper] (.getElementById js/document "app")))

(defn init!
  []
  (mount-root))
