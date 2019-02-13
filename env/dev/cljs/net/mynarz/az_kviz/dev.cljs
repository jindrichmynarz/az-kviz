(ns ^:figwheel-no-load net.mynarz.az-kviz.dev
  (:require [net.mynarz.az-kviz.core :as core]
            [cljs.spec.test.alpha :as ts]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)

(ts/instrument)
