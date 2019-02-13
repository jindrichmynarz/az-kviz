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
          (range-1 [n]
            (range 1 (inc n)))
          (row [y length]
            (map vector (range length) (repeat y)))
          (tile [text coords]
            {:coords coords
             :status :default
             :text text})]
    (fn [side]
      (let [size (triangular-number side)
            indices (map str (range-1 size))
            points (->> (range-1 side)
                        (map-indexed row)
                        (apply concat))]
        (mapv tile indices points)))))

; What is below is just to preview the components

(def state
  (r/atom (init-board-state 7)))

;; -------------------------
;; Views

(defn wrapper
  []
  [board {} @state])

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (r/render [wrapper] (.getElementById js/document "app")))

(defn init!
  []
  (mount-root))
