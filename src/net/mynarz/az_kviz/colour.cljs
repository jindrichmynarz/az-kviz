(ns net.mynarz.az-kviz.colour
  (:require [thi.ng.color.core :as color]
            [thi.ng.math.core :as math]))

(def colours
  {:active "#eee"
   :default "#fefefe"
   :hover "#fff"
   :hover-missed "#575757"
   :missed "#020100"
   :player-1 "#4dbbc6"
   :player-2 "#f25d00"})

(defn darken
  "Darken a CSS `colour`, such as #ff69b4, by `amount` from [0, 1]."
  [colour amount]
  (-> (color/css colour)
      (math/mix color/BLACK amount)
      color/as-css
      deref))
