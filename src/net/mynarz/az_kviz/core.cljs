(ns net.mynarz.az-kviz.core
  (:require [net.mynarz.az-kviz.spec :as spec]
            [net.mynarz.az-kviz.view :refer [board]]
            [cljs.spec.alpha :as s]
            [reagent.core :as r]))

(s/fdef init-board-state
        :args (s/cat :side ::spec/side)
        :ret ::spec/board-state)
(def init-board-state
  "Initialize the state for board with `side` length."
  (letfn [(triangular-number [n]
            (/ (* n (inc n)) 2))
          (row [y length]
            (map vector (range length) (repeat y)))
          (get-sides [side [x y]]
            (cond-> #{}
              (zero? x) (conj :a)
              (= x y) (conj :b)
              (= y side) (conj :c)))
          (tile [side id coords]
            {:coords coords
             :id id
             :sides (get-sides side coords)
             :status :default
             :text (-> id inc str)})]
    (fn [side]
      (let [size (triangular-number side)
            indices (range size)
            points (->> (inc size)
                        (range 1)
                        (map-indexed row)
                        (apply concat))]
        (mapv (partial tile side) indices points)))))

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
