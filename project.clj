(defproject net.mynarz/az-kviz "0.2"
  :description "Reagent component for the AZ-kvíz board"
  :url "https://github.com/jindrichmynarz/az-kviz"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojurescript "1.11.132" :scope "provided"]
                 [reagent "1.2.0"]
                 [thi.ng/color "1.5.1"]
                 [thi.ng/geom "1.0.1"]
                 [thi.ng/math "0.3.2"]]
  :source-paths ["src"]
  :profiles {:dev {:clean-targets ^{:protect false} ["target"]
                   :dependencies [[binaryage/devtools "1.0.7"]
                                  [org.clojure/test.check "1.1.1"]]
                   :resource-paths ["target"]}})
