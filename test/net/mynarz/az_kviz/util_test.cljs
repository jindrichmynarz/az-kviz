(ns net.mynarz.az-kviz.util-test
  (:require [net.mynarz.az-kviz.spec :as spec]
            [net.mynarz.az-kviz.util :as util]
            [cljs.spec.alpha :as s]
            [clojure.test.check]
            [clojure.test.check.clojure-test :refer-macros [defspec]]
            [clojure.test.check.generators]
            [clojure.test.check.properties :refer-macros [for-all]]))

(defspec triangular-number-there-and-back
  10
  (for-all [side (s/gen ::spec/side)]
    (= side
       (-> side
           util/triangular-number
           util/triangle-size->side))))

