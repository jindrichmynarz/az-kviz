(ns net.mynarz.az-kviz.prod
  (:require [net.mynarz.az-kviz.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
