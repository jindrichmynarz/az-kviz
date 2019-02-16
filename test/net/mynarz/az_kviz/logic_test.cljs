(ns net.mynarz.az-kviz.logic-test
  (:require [net.mynarz.az-kviz.core :as core]
            [net.mynarz.az-kviz.logic :as logic]
            [net.mynarz.az-kviz.util :as util]
            [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as ts]
            [clojure.test :refer [are deftest testing]]))

(ts/instrument)

(defn- init-board
  "Initialize a game board with `side` length."
  [side]
  (mapv merge
        (logic/init-board-data side)
        (core/init-board-state side)))

(defn- board-state
  "Get the state of board with `statuses` set."
  [statuses]
  (let [side (util/triangle-size->side (count statuses))]
    (assert (integer? side) "Invalid number of statuses. Cannot create a board.")
    (mapv (fn [tile status] (assoc tile :status status))
          (init-board side)
          statuses)))

(def board-3-no-player
  (init-board 3))

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

(deftest in-board?
  (are [side coord pred] (pred (logic/in-board? side coord))
       3 [2 2] true?
       3 [3 3] false?))

(deftest owns-all-sides?
  (letfn [(by-player [player board]
            (filter (comp #{player} :status) board))]
    (are [board player] (logic/owns-all-sides? (by-player player board))
          board-3-player-1 :player-1
          board-3-player-2 :player-2
          board-4-player-1 :player-1
          board-4-player-2 :player-2)
    (are [board player] (not (logic/owns-all-sides? (by-player player board)))
          board-3-no-player :player-1
          board-3-no-player :player-2)))

(deftest player-won?
   (testing "if player won"
     (are [board winner won?] (won? (logic/player-won? winner board))
          board-3-player-1 :player-1 true?
          board-3-player-1 :player-2 false?
          board-3-player-2 :player-2 true?
          board-3-player-2 :player-1 false?
          board-3-no-player :player-1 false?
          board-3-no-player :player-2 false?
          board-4-player-1 :player-1 true?
          board-4-player-1 :player-2 false?
          board-4-player-2 :player-2 true?
          board-4-player-2 :player-1 false?)))
