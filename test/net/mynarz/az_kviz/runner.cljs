(ns net.mynarz.az-kviz.runner
  (:require [net.mynarz.az-kviz.logic-test]
            [net.mynarz.az-kviz.util-test]
            [cljs-test-display.core]
            [figwheel.main.testing :refer-macros [run-tests]]))

(run-tests (cljs-test-display.core/init! "app-testing"))
