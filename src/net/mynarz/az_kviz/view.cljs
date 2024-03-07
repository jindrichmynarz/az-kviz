(ns net.mynarz.az-kviz.view
  (:require [net.mynarz.az-kviz.logic :as logic]
            [net.mynarz.az-kviz.spec :as spec]
            [net.mynarz.az-kviz.util :as util]
            [cljs.spec.alpha :as s]
            [goog.string.format]
            [thi.ng.color.core :as color]
            [thi.ng.geom.svg.core :as svg]
            [thi.ng.math.core :as math]))

; ----- Default configuration -----

(def board-config
  {:on-click (fn [_])
   :side 7
   :tile-config {:colours {:active "#dedede"
                           :default "#ccc"
                           :hover "#dedede"
                           :hover-missed "#4d4d4d"
                           :missed "#333"
                           :player-1 "#e40000"
                           :player-2 "#354d65"}
                 :hex-shade 0.8
                 :inner-hex-size 0.87
                 :radius 35
                 :spacing 0.05
                 :stroke-width 1}})

; ----- Aliases ----

(def format
  goog.string.format)

(def right-angle
  (/ js/Math.PI 2))

(def sqrt-3
  (js/Math.sqrt 3))

; ----- Static markup -----

(def drop-shadow
  [:filter#drop-shadow
   [:feDropShadow {:dx 1
                   :dy 1
                   :floodColor "#000"
                   :stdDeviation 2}]])

(def white-etch
  [:filter#white-etch
   [:feDropShadow {:dx 0
                   :dy 2
                   :floodColor "#fff"
                   :stdDeviation 0}]])

; ----- Private functions -----

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
  `center` are the [x y] coordinates of the tile.
  `inner` is the collection of [x y] points for the inner hexagon.
  `outer` is the collection of [x y] points for the outer hexagon.
  `radius` is the tile radius from its centre to its edge.
  `status` is the state of the tile as a keyword.
  `svg` is the custom SVG markup to show in the tile.
  `svg-attrs` are the pre-computed attributes (position) for `svg`.
  `text` is the text to show in the tile."
  [{:keys [center classes id inner outer
           radius status svg svg-attrs text]}]
  (let [outer-fill (format "url(#%s-outer)" (name status))
        inner-fill (format "url(#%s-inner)" (name status))
        font-size (if (< (count text) 3) radius (* radius (/ 2 3)))
        available? (not (#{:player-1 :player-2} status))
        all-classes (cond-> (conj classes "tile" (name status))
                      available? (conj "available"))
        transform-origin (apply format "%dpx %dpx" center)]
    [svg/group {:id id
                :class all-classes
                :style {:transform-origin transform-origin}}
     ^{:key "outer"}
     [svg/polygon outer {:class "outer"
                         :fill outer-fill}]
     ^{:key "inner"}
     [svg/polygon inner {:class "inner"
                         :fill inner-fill}]
     (if svg
       (->> svg
            (drop 2)
            (cons svg-attrs)
            (cons :svg)
            vec)
       [svg/text center text {:font-size font-size}])]))

(defn- status-gradients
  "Generate SVG gradients for the given `status` and `colour`.
  Gradient shading amount is controlled by `hex-shade`."
  [hex-shade [status colour]]
  (let [lighter-colour (lighten colour hex-shade)
        stop (fn [offset colour]
               [:stop {:offset (str offset "%")
                       :stop-color colour}])
        start (stop 0 colour)
        end (stop 100 lighter-colour)
        gradient (fn [suffix attrs]
                   (let [id (str (name status) "-" suffix)]
                     ^{:key id}
                     [:linearGradient (merge {:id id} attrs) start end]))]
    [(gradient "inner" {:x1 0 :x2 0 :y1 0 :y2 1})
     (gradient "outer" {:x1 0 :x2 0 :y1 1 :y2 0})]))

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
                 spacing
                 stroke-width]} :tile-config
         :keys [on-click]
         n :side} (if (seq config)
                    (util/deep-merge board-config config)
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
                      (let [[x y :as center] [(x-offset coords) (y-offset coords)]
                            side (* r (- 3 sqrt-3))
                            inner-side (* side 0.8) ; side minus padding
                            half-side (* inner-side 0.5)]
                        {:center center
                         :id id
                         :inner (hexagon inner-r center)
                         :outer (hexagon r center)
                         :radius r
                         :svg-attrs {:width inner-side
                                     :height inner-side
                                     :view-box [0 0 100 100]
                                     :x (- x half-side)
                                     :y (- y half-side)}}))
        board-data (map tile-points state)
        gradients (mapcat (partial status-gradients hex-shade) colours)
        click-handler (fn [e]
                        (when-let [tile (.closest (.-target e) "g.tile.available")]
                          (on-click (js/parseInt (.-id tile)))))
        tile-fn (fn [{:keys [id] :as args}] ^{:key id} [tile args])]
    (fn [_ state]
      [:svg#az-kviz {:on-click click-handler
                     :viewBox [0 0 board-width board-height]}
       [:defs drop-shadow white-etch gradients]
       (map (comp tile-fn merge) board-data state)])))
