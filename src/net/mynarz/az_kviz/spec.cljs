(ns net.mynarz.az-kviz.spec
  (:require [cljs.spec.alpha :as s]))

(s/def ::pos-int
  ; Positive whole number
  (s/and integer? pos?))

(s/def ::pos-num
  ; Positive number
  (s/and double? pos?))

(s/def ::unit-interval
  ; Inclusive interval between 0 and 1.
  (s/and ::pos-num (partial > 1)))

(s/def ::side
  ; Number of tiles per side of a board
  ::pos-int)

(s/def ::point
  ; [x, y] point
  (s/tuple ::pos-num ::pos-num))

(s/def ::points
  (s/coll-of ::point))

(s/def ::status
  ; Status of a tile
  #{:active
    :default 
    :hover
    ::hover-missed
    ::missed
    ::player-1
    ::player-2})

(s/def ::text
  ; Tile text, recommended to be max 4 characters
  (s/and string? (comp (partial >= 4) count)))

(s/def ::tile
  (s/keys :req-un [::points
                   ::status
                   ::text]))

(s/def ::board-state
  ; State of an AZ-kvíz board
  (s/coll-of ::tile
             :kind vector?
             :min-count 1))

(s/def ::inner-hex-size
  ; Size of tile's inner hexagon as a fraction of the outer hexagon's size
  ::unit-interval)

(s/def ::radius
  ; Radius from a tile centre to its edge
  ::pos-num)

(s/def ::spacing
  ; Size of spaces between tiles as a fraction of tile radius.
  ::unit-interval)

(s/def ::stroke-width
  ; Tile border width
  ::pos-int)

(s/def ::tile-config
  (s/keys :opt-un [::inner-hex-size
                   ::radius
                   ::spacing
                   ::stroke-width]))

(s/def ::board-config
  (s/keys :opt-un [::side ::tile-config]))

(s/def ::any any?)

(s/def ::hiccup
  ; Hiccup representation of HTML
  ; We don't go into the details, any vector will do.
  (s/coll-of ::any
             :kind vector?))
