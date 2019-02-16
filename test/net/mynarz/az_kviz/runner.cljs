;; This test runner is intended to be run from the command line
(ns net.mynarz.az-kviz.runner
  (:require [net.mynarz.az-kviz.core-test]
            [net.mynarz.az-kviz.logic-test]
            [net.mynarz.az-kviz.util-test]
            [figwheel.main.testing :refer-macros [run-tests-async]]))

(enable-console-print!)

(defn -main
  [& args]
  (run-tests-async 10000))
