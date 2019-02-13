(ns net.mynarz.az-kviz.core
  (:require [net.mynarz.az-kviz.view :refer [board init-board-state]]
            [reagent.core :as r]))

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
