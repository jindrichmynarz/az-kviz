(ns net.mynarz.az-kviz.core
  (:require [net.mynarz.az-kviz.logic :as logic]
            [net.mynarz.az-kviz.view :refer [board]]
            ["react" :as react]
            [reagent.core :as r]
            [reagent.dom.client :as rdc]))

; What is below is just to preview the components

(defn dev-setup
  []
  (when goog.DEBUG
    (enable-console-print!) ;; so that println writes to `console.log`
    (println "dev mode")))

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

(defonce root
  ;; Init only on use, this ns is loaded for SSR build also
  (delay (rdc/create-root (js/document.getElementById "app"))))

(defn run
  []
  (dev-setup)
  (when r/is-client
    ;; Enable StrictMode to warn about e.g. findDOMNode
    (rdc/render @root [:> react/StrictMode {} [wrapper]])))

(defn ^:export main
  []
  (run))
