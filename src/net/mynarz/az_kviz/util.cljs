(ns net.mynarz.az-kviz.util)

(defn deep-merge
  "Deep merge `maps`."
  [& maps]
  (if (every? map? maps)
    (apply merge-with deep-merge maps)
    (last maps)))

(defn triangle-size->side
  [size]
  (/ (dec (js/Math.sqrt (inc (* 8 size))))
     2))

(defn triangular-number
  [n]
  (/ (* n (inc n)) 2))

