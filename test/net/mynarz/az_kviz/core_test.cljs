(ns net.mynarz.az-kviz.core-test
  (:require [net.mynarz.az-kviz.core :as core]
            [net.mynarz.az-kviz.spec :as spec]
            [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as ts]
            [clojure.test :refer [are deftest testing]]
            [clojure.test.check] 
            [clojure.test.check.clojure-test :refer-macros [defspec]]
            [clojure.test.check.generators]
            [clojure.test.check.properties :refer-macros [for-all]]))

(ts/instrument)

(defspec triangular-number-there-and-back
  10
  (for-all [side (s/gen ::spec/side)]
    (= side
       (-> side
           core/triangular-number
           core/triangle-size->side))))

(defn- board-state
  "Get the state of board with `side` where `statuses` are set."
  [statuses]
  (let [side (core/triangle-size->side (count statuses))]
    (assert (integer? side) "Invalid number of statuses. Cannot create a board.")
    (mapv (fn [tile status] (assoc tile :status status))
          (core/init-board-state side)
          statuses)))

(def board-3-no-player
  (core/init-board-state 3))

(def board-3-player-1
  (board-state [:player-1
                :player-1 :player-2
                :player-2 :player-1 :missed]))

(def board-3-player-2
  (board-state [:player-1
                :player-1 :player-1
                :player-2 :player-2 :player-2]))

(def board-4-player-1
  (board-state [:player-2
                :player-1 :player-2
                :player-2 :player-1 :player-1
                :player-1 :player-1 :player-2 :player-1]))

(def board-4-player-2
  (board-state [:player-2
                :player-1 :player-1
                :player-1 :player-2 :player-2
                :player-2 :player-2 :player-1 :player-1]))

(deftest owns-all-sides?
  (letfn [(by-player [player board]
            (filter (comp #{player} :status) board))]
    (are [board player] (core/owns-all-sides? (by-player player board))
          board-3-player-1 :player-1
          board-3-player-2 :player-2
          board-4-player-1 :player-1
          board-4-player-2 :player-2)
    (are [board player] (not (core/owns-all-sides? (by-player player board)))
          board-3-no-player :player-1
          board-3-no-player :player-2)))

(deftest player-won?
   (testing "if player won"
     (are [board winner] (core/player-won? winner board)
          board-3-player-1 :player-1
          board-3-player-2 :player-2
          board-4-player-1 :player-1
          board-4-player-1 :player-2)))
