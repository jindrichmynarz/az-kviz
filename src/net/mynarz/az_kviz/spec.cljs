(ns net.mynarz.az-kviz.spec
  (:require [cljs.spec.alpha :as s]))

(s/def ::non-negative-int
  (s/and integer? (complement neg?)))

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

(s/def ::coord ::non-negative-int)

(s/def ::coords
  ; [x, y] coordinates
  (s/tuple ::coord ::coord))

(s/def ::id ::non-negative-int)

(s/def ::neighbours
  (s/coll-of ::coords :kind set?))

(s/def ::sides
  (s/coll-of #{:a :b :c} :kind set?))

(s/def ::status
  ; Status of a tile
  keyword?)

(s/def ::text
  ; Tile text, recommended to be max 4 characters
  (s/and string? (comp (partial >= 4) count)))

(s/def ::tile-state
  (s/keys :req-un [::coords
                   ::id
                   ::neighbours
                   ::sides
                   ::status
                   ::text]))

(s/def ::board-state
  ; State of an AZ-kvíz board
  (s/coll-of ::tile-state
             :min-count 1
             :kind vector?))

(s/def ::colour
  (s/and string?
         (comp #{4 7} count)
         (s/conformer seq)
         (s/cat :hash #{"#"}
                :colour (s/+ (set "0123456789abcdef")))))

(s/def ::colours
  ; Colours associated with tile statuses
  (s/keys keyword? ::colour))

(s/def ::hex-shade
  ; Amount of shading applied to the tile colour
  ::unit-interval)

(s/def ::inner-hex-size
  ; Size of tile's inner hexagon as a fraction of the outer hexagon's size
  ::unit-interval)

(s/def ::on-click
  ; Function to handle clicks on tiles. Get tile ID as an argument.
  (s/fspec :args (s/cat :id ::id)
           :ret nil?))

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
  (s/keys :opt-un [::colours
                   ::hex-shade
                   ::inner-hex-size
                   ::radius
                   ::spacing
                   ::stroke-width]))

(s/def ::board-config
  (s/keys :opt-un [::on-click
                   ::side
                   ::tile-config]))

(s/def ::any any?)

(s/def ::hiccup
  ; Hiccup representation of HTML
  ; We don't go into the details, any vector will do.
  (s/coll-of ::any
             :kind vector?))

(s/def ::player
  #{:player-1 :player-2})
