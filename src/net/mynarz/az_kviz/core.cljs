(ns net.mynarz.az-kviz.core
  (:require [net.mynarz.az-kviz.logic :as logic]
            [net.mynarz.az-kviz.view :refer [board]]
            [reagent.core :as r]
            [reagent.dom :as rd]))

; What is below is just to preview the components

(def state
  (r/atom (logic/init-board-state)))

;; -------------------------
;; Views

(defn wrapper
  []
  [board {:on-click (fn [id]
                      (swap! state
                             update-in
                             [id :status]
                             {:default :active
                              :active :default}))}
        @state])

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (rd/render [wrapper] (.-body js/document)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-root)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-root))
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
