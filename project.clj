(defproject net.mynarz.az-kviz "0.1.3"
  :description "Reagent component for the AZ-kvíz board"
  :url "https://github.com/jindrichmynarz/az-kviz"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.10.516"]
                 [reagent "1.2.0"]
                 [cljsjs/react "18.2.0-1"]
                 [cljsjs/react-dom "18.2.0-1"]
                 [thi.ng/math "0.2.2"]
                 [thi.ng/color "1.3.0"]
                 [thi.ng/geom "1.0.0-RC3"]]
  :source-paths ["src"]
  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:dev"   ["trampoline" "run" "-m" "figwheel.main" "--build" "dev" "--repl"]
            "fig:test"  ["trampoline" "run" "-m" "figwheel.main" "--build" "test" "--serve"]}
  :profiles {:dev {:clean-targets ^{:protect false} ["target"]
                   :dependencies [[binaryage/devtools "1.0.7"]
                                  [com.bhauman/cljs-test-display "0.1.1"]
                                  [com.bhauman/figwheel-main "0.2.18"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]
                                  [org.clojure/test.check "1.1.1"]]
                   :resource-paths ["target"]}})
