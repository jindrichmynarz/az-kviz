(ns net.mynarz.az-kviz.logic
  (:require [net.mynarz.az-kviz.spec :as spec]
            [net.mynarz.az-kviz.util :as util]
            [cljs.spec.alpha :as s]
            [clojure.set :refer [union]]))

(defn in-board?
  "Given a board with `side` length,
  test if coordinate [x y] is in the board."
  [side [x y]]
  (letfn [(in? [coord] (and (>= coord 0) (< coord side)))]
    (and (<= x y)
         (in? x)
         (in? y))))

(def neighbours
  "Given a board with `side` length,
  return neighbours of coordinate `coords`."
  (let [possible-offsets [[-1 -1] [0 -1] [1 0] [0 1] [1 1] [-1 0]]]
    (fn [side coords]
      (when (in-board? side coords)
        (->> possible-offsets
             (map (partial mapv + coords))
             (filter (partial in-board? side))
             (into #{}))))))

(defn sides
  "Given a board with `side` length,
  return the board sides a coordinate [x y] is at."
  [side [x y]]
  (cond-> #{}
    (zero? x) (conj :a)
    (= x y) (conj :b)
    (= y (dec side)) (conj :c)))

(defn init-tile-state
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
        :args (s/? ::spec/side)
        :ret ::spec/board-state)
(def init-board-state
  "Initialize the data for board with `side` length.
  Defaults to side lenght = 7, just like in AZ-kvíz."
  (letfn [(row [y length]
            (map vector (range length) (repeat y)))]
    (fn ([]
         (init-board-state 7))
        ([side]
         (let [size (util/triangular-number side)
               ids (range size)
               coords (->> (inc size)
                           (range 1)
                           (map-indexed row)
                           (apply concat))]
           (mapv (partial init-tile-state side)
                 ids
                 coords))))))

(defn owns-all-sides?
  "Test if played owning `tiles` reached all sides of the board."
  [tiles]
  (->> tiles
       (map :sides)
       (reduce union)
       (= #{:a :b :c})))

(defn coords->tile
  "Look up tile by its `coords` in `tiles`."
  [tiles coords]
  (first (filter (comp #{coords} :coords) tiles)))

(defn all-sides-connected-from-tile?
  "Test if `tiles` connect all sides of the board, starting from `tile`."
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
  "Test if `tiles` connect all sides of the board."
  [tiles]
  (->> tiles
       (filter (comp seq :sides))
       (some (partial all-sides-connected-from-tile? tiles))
       true?))

(s/fdef player-won?
        :args (s/cat :board-state ::spec/board-state
                     :player ::spec/player)
        :ret boolean?)
(defn player-won?
  "Given a `board-state` test if `player` has won the game."
  [board-state player]
  (let [owned-tiles (filter (comp #{player} :status) board-state)
        side (util/triangle-size->side (count board-state))]
    (and (<= side (count owned-tiles))
         (owns-all-sides? owned-tiles)
         (all-sides-connected? owned-tiles))))

(s/fdef who-won
        :args (s/cat :board-state ::spec/board-state)
        :ret (s/nilable ::spec/player))
(defn who-won
  "Given a `board-state`, return the player who won or nil when there is no winner."
  [board-state]
  (->> [:player-1 :player-2]
       (filter (partial player-won? board-state))
       first))
