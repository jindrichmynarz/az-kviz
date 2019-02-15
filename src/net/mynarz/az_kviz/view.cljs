(ns net.mynarz.az-kviz.view
  (:require [net.mynarz.az-kviz.spec :as spec]
            [cljs.spec.alpha :as s]
            [goog.string.format]
            [thi.ng.color.core :as color]
            [thi.ng.geom.svg.adapter :as adapt] 
            [thi.ng.geom.svg.core :as svg]
            [thi.ng.math.core :as math]))

; ----- Default configuration -----

(def board-config
  {:side 7
   :tile-config {:colours {:active "#dedede"
                           :default "#ccc"
                           :hover "#dedede"
                           :hover-missed "#4d4d4d"
                           :missed "#333"
                           :player-1 "#e40000"
                           :player-2 "#354d65"}
                 :hex-shade 0.8
                 :inner-hex-size 0.87
                 :on-click (fn [_])
                 :radius 35
                 :spacing 0.05
                 :stroke-width 1}})

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
                   :floodColor "#0000080"
                   :stdDeviation 2}]])

; ----- Private functions -----

(defn- deep-merge
  "Deep merge `maps`."
  [& maps]
  (if (every? map? maps)
    (apply merge-with deep-merge maps)
    (last maps)))

(defn- lighten
  "Lighten a CSS `colour`, such as #ff69b4, by `amount` from [0, 1]."
  [colour amount]
  (-> (color/css colour)
      (math/mix color/WHITE amount)
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
  `on-click` is a function handling click events that receive tile ID as the argument.
  `center` are the [x y] coordinates of the tile.
  `inner` is the collection of [x y] points for the inner hexagon.
  `outer` is the collection of [x y] points for the outer hexagon.
  `radius` is the tile radius from its centre to its edge.
  `status` is the state of the tile as a keyword.
  `text` is the text to show in the tile."
  [on-click
   {:keys [center id inner outer radius]}
   {:keys [status text]}]
  (let [outer-fill (format "url(#%s-outer)" (name status))
        inner-fill (format "url(#%s-inner)" (name status))
        font-size (if (< (count text) 3) radius (* radius (/ 2 3)))
        available? (= status :default)
        classes (cond-> ["tile"] available? (conj "available"))]
    [svg/group (cond-> {:class classes}
                  available? (assoc :on-click (fn [_] (on-click id))))
     [svg/polygon outer 
                  {:class "outer"
                   :fill outer-fill}]
     [svg/polygon inner
                  {:class "inner"
                   :fill inner-fill}]
     [svg/text center
               text
               {:font-size font-size}]]))

(defn- status-gradients
  "Generate SVG gradients for the given `status` and `colour`.
  Gradient shading amount is controlled by `hex-shade`."
  [hex-shade [status colour]]
  (let [start [0 colour]
        end [100 (lighten colour hex-shade)]
        gradient (fn [status suffix attrs]
                   (let [id (str (name status) suffix)]
                     (svg/linear-gradient id attrs start end)))] 
    [(gradient status "-inner" {:x1 0 :x2 0 :y1 0 :y2 1})
     (gradient status "-outer" {:x1 0 :x2 0 :y1 1 :y2 0})]))

; ----- Public functions -----

(s/def ::board-args
  (s/cat :tile-config ::spec/tile-config
         :board-state ::spec/board-state))
(s/fdef board
        :args ::board-args
        :ret (s/fspec :args ::board-args
                      :ret ::spec/hiccup))
(defn board
  [config state]
  (let [{{r :radius
          :keys [colours
                 hex-shade
                 inner-hex-size
                 on-click
                 spacing
                 stroke-width]} :tile-config
         n :side} (if (seq config)
                    (deep-merge board-config config)
                    board-config)
        padding r ; Radius as padding
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
        tile-points (fn [{:keys [coords id]}]
                      (let [center [(x-offset coords) (y-offset coords)]]
                        {:center center
                         :id id
                         :inner (hexagon inner-r center)
                         :outer (hexagon r center)
                         :radius r}))
        tiles (map tile-points state)
        gradients (mapcat (partial status-gradients hex-shade) colours)
        make-tile (partial tile on-click)]
    (fn [_ state]
      (adapt/inject-element-attribs
        [:svg#az-kviz {:viewBox [0 0 board-width board-height]}
         [:defs drop-shadow gradients]
         (map make-tile tiles state)]))))
