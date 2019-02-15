(ns ^:figwheel-no-load net.mynarz.az-kviz.dev
  (:require [net.mynarz.az-kviz.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
