(ns net.mynarz.az-kviz.logic
  (:require [net.mynarz.az-kviz.spec :as spec]
            [net.mynarz.az-kviz.util :as util]
            [cljs.spec.alpha :as s]
            [clojure.set :refer [union]]))

(defn in-board?
  "Given a board with `side` length,
  test if coordinate [x y] is in the board."
  [side [x y]]
  (and (<= x y)
       (<= 0 x side)
       (<= 0 y side)))

(def neighbours
  "Given a board with `side` length,
  return neighbours of coordinate `coords`."
  (let [possible-offsets [[-1 -1] [0 -1] [1 0] [0 1] [1 1] [-1 0]]
        offset (partial mapv +)]
    (fn [side coords]
      (->> possible-offsets
           (map (partial offset coords))
           (filter (partial in-board? side))
           (into #{})))))

(defn sides
  "Given a board with `side` length,
  return the board sides a coordinate [x y] is at."
  [side [x y]]
  (cond-> #{}
    (zero? x) (conj :a)
    (= x y) (conj :b)
    (= y (dec side)) (conj :c)))

(defn init-tile-data
  "Initialize data of a tile in a board with `side` length,
  The tile has `id` and is located as `coords`."
  [side id coords]
  {:coords coords
   :id id
   :neighbours (neighbours side coords)
   :sides (sides side coords)})

(s/fdef init-board-data
        :args (s/cat :side ::spec/side)
        :ret ::spec/board-state)
(def init-board-data
  "Initialize the data for board with `side` length."
  (letfn [(row [y length]
            (map vector (range length) (repeat y)))
          (neighbours [side [x y]])]
    (fn [side]
      (let [size (util/triangular-number side)
            ids (range size)
            coords (->> (inc size)
                        (range 1)
                        (map-indexed row)
                        (apply concat))]
        (mapv (partial init-tile-data side) ids coords)))))

(defn owns-all-sides?
  "Test if played owning `tiles` reached all sides of the board."
  [tiles]
  (->> tiles
       (map :sides)
       (reduce union)
       (= #{:a :b :c})))

(defn coords->tile
  [tiles coords]
  (first (filter (comp #{coords} :coords) tiles)))

(defn reaches-all-sides?
  [tiles tile]
  (let [owns? (->> tiles
                   (map :coords)
                   (into #{}))
        ->tile (partial coords->tile tiles)]
    (loop [visited-sides (:sides tile)
           visited-coords #{(:coords tile)}
           coords-to-visit (filter owns? (:neighbours tile))]
      (or (= visited-sides #{:a :b :c})
          (and (seq coords-to-visit)
               (let [next-coords (first coords-to-visit)
                     {:keys [neighbours sides]} (->tile next-coords)]
                (recur (union visited-sides sides)
                       (conj visited-coords next-coords)
                       (->> neighbours
                            (filter (every-pred (complement visited-coords) owns?))
                            (into (rest coords-to-visit))))))))))

(defn all-sides-connected?
  [tiles]
  (->> tiles
       (filter (comp seq :sides))
       (some (partial reaches-all-sides? tiles))))

(s/fdef player-won?
        :args (s/cat :player ::spec/player
                     :board-state ::spec/board-state)
        :ret boolean?)
(defn player-won?
  "Given a `board-state` test if `player` has won the game."
  [player board-state]
  (let [owned-tiles (filter (comp #{player} :status) board-state)
        side (util/triangle-size->side board-state)]
    (and (>= side (count owned-tiles))
         (owns-all-sides? owned-tiles)
         (all-sides-connected? owned-tiles))))
