(ns net.mynarz.az-kviz.runner
  (:require [net.mynarz.az-kviz.core-test]
            [doo.runner :refer-macros [doo-tests]]))

(enable-console-print!)

(doo-tests 'net.mynarz.az-kviz.core-test)
