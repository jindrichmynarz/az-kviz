(ns net.mynarz.az-kviz.view
  (:require [net.mynarz.az-kviz.spec :as spec]
            [cljs.spec.alpha :as s]
            [goog.string.format]
            [thi.ng.color.core :as color]
            [thi.ng.geom.svg.adapter :as adapt] 
            [thi.ng.geom.svg.core :as svg]
            [thi.ng.math.core :as math]))

; ----- Default configuration -----

(def colours
  {:active "#eee"
   :default "#fefefe"
   :hover "#fff"
   :hover-missed "#575757"
   :missed "#020100"
   :player-1 "#4dbbc6"
   :player-2 "#f25d00"})

; ----- Aliases ----

(def format
  goog.string.format)

(def right-angle
  (/ js.Math.PI 2))

(def sqrt-3
  (.sqrt js/Math 3))

; ----- Static markup -----

(def drop-shadow
  [:filter#drop-shadow
   [:feDropShadow {:dx 1
                   :dy 1
                   :floodColor "rgba(255,105,180,0.5)"
                   :stdDeviation 3}]])

; ----- Private functions -----

(defn- darken
  "Darken a CSS `colour`, such as #ff69b4, by `amount` from [0, 1]."
  [colour amount]
  (-> (color/css colour)
      (math/mix color/BLACK amount)
      color/as-css
      deref))

(defn- regular-polygon
  "Generate points of a regular polygon that has a number of `sides`,
  is centered at the `center` [x, y], and spans the `radius`."
  [sides radius [x y]]
  (let [inner-angle (/ (* 2 js/Math.PI) sides)
        point-x (fn [angle]
                  (->> angle
                       (.cos js/Math)
                       (* radius)
                       (+ x)))
        point-y (fn [angle]
                  (->> angle
                       (.sin js/Math)
                       (* radius)
                       (+ y)))
        point (fn [side]
                (let [angle (->> side
                                 (* inner-angle)
                                 (+ right-angle))]
                  [(point-x angle)
                   (point-y angle)]))]
      (->> sides
           inc
           range
           rest
           (map point))))

(def hexagon
  (partial regular-polygon 6))

(defn- tile
  "An AZ-kvíz tile.
  `center` are the [x y] coordinates of the tile.
  `inner` is the collection of [x y] points for the inner hexagon.
  `outer` is the collection of [x y] points for the outer hexagon.
  `radius` is the tile radius from its centre to its edge.
  `status` is the state of the tile as a keyword.
  `text` is the text to show in the tile."
  [{:keys [center inner outer radius]}
   {:keys [status text]}]
  (let [outer-fill (format "url(#%s-outer)" (name status))
        inner-fill (format "url(#%s-inner)" (name status))
        font-size (if (< (count text) 3) radius (* radius (/ 2 3)))]
    [:g.tile {}
     [svg/polygon outer 
                  {:class "outer"
                   :fill outer-fill}]
     [svg/polygon inner
                  {:class "inner"
                   :fill inner-fill}]
     [svg/text center text {:font-size font-size}]]))

(defn- status-gradients
  "Generate SVG gradients for the given `status` and `colour`."
  [{:keys [hex-shade]
    :or {hex-shade 0}}
   [status colour]]
  (let [start [0 colour]
        end [100 (darken colour hex-shade)]
        gradient (fn [status suffix attrs]
                   (let [id (str (name status) suffix)]
                     (svg/linear-gradient id attrs start end)))] 
    [(gradient status "-inner" {:x1 0 :x2 0 :y1 0 :y2 1})
     (gradient status "-outer" {:x1 0 :x2 0 :y1 1 :y2 0})]))

(defn- gradients
  [{:keys [status-colours]
    :or {status-colours colours}
    :as config}]
  (mapcat (partial status-gradients config) status-colours))

; ----- Public functions -----

(s/def ::board-args
  (s/cat :tile-config ::spec/tile-config
         :board-state ::spec/board-state))
(s/fdef board
        :args ::board-args
        :ret (s/fspec :args ::board-args
                      :ret ::spec/hiccup))
(defn board
  [{{r :radius
     :keys [inner-hex-size
            spacing
            stroke-width]
     :or {inner-hex-size 0.87
          r 35
          spacing 0.07
          stroke-width 1}} :tile-config
    n :side
    :or {n 7}}
   state]
  (let [padding r ; Radius as padding
        inner-r (* r inner-hex-size) ; Tile's inner hexagon's radius
        y-space (* 2 r spacing) ; Vertical space between tiles
        x-space (* y-space (/ sqrt-3 2)) ; Horizontal space between tiles
        w (+ (* sqrt-3 r) ; Tile width
             (* 2 stroke-width)) ; Account for hexagon borders
        h (+ (* 2 r) ; Tile height
             (* 2 stroke-width))
        board-width (+ (* n w)
                       (* (inc n) x-space)
                       (* 2 padding)) ; Add width for spaces
        board-height (+ (* 2 r)
                        (* (/ 3 2) (dec n) r)
                        (* (inc n) y-space)
                        (* 2 padding)) ; Add height for spaces
        x-offset (fn [[x y]]
                   (+ (/ w 2)
                      (* x (+ w x-space))
                      (* (- n (inc y)) (/ w 2))
                      (* (- n y) (/ x-space 2))
                      padding))
        y-offset (fn [[_ y]]
                   (+ r
                      (* (/ 3 2) r y)
                      (* (inc y) y-space) ; Account for spaces
                      padding))
        tile-points (fn [{:keys [coords]}]
                      (let [center [(x-offset coords) (y-offset coords)]]
                        {:center center
                         :inner (hexagon inner-r center)
                         :outer (hexagon r center)
                         :radius r}))
        tiles (map tile-points state)]
    (fn [config state]
      (adapt/inject-element-attribs
        [:svg#az-kviz {:x 0
                       :y 0
                       :width board-width
                       :height board-height}
         [:defs drop-shadow (gradients config)]
         (map tile tiles state)]))))
