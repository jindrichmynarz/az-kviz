(ns net.mynarz.az-kviz.core
  (:require [net.mynarz.az-kviz.logic :as logic]
            [net.mynarz.az-kviz.spec :as spec]
            [net.mynarz.az-kviz.util :as util] 
            [net.mynarz.az-kviz.view :refer [board]]
            [cljs.spec.alpha :as s]
            [reagent.core :as r]))

(s/fdef init-board-state
        :args (s/cat :side ::spec/side)
        :ret ::spec/board-state)
(def init-board-state
  "Get an initial board state for board with `side` length."
  (let [->tile (comp (partial assoc {:status :default} :text) str inc)]
    (fn [side]
      (->> side
           util/triangular-number
           range
           (mapv ->tile)))))

; What is below is just to preview the components

(def state
  (r/atom (init-board-state 7)))

;; -------------------------
;; Views

(defn wrapper
  []
  [board {:tile-config {:on-click (fn [id]
                                    (swap! state assoc-in [id :status] :player-1)
                                    (js/console.log (logic/player-won? :player-1 @state)))}}
         @state])

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (when-let [root (.getElementById js/document "app")]
    (r/render [wrapper] root)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-root)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-root))
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
