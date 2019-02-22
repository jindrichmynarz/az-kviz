(defproject net.mynarz.az-kviz "0.1.0"
  :description "Reagent component for the AZ-kvíz board"
  :url "https://github.com/jindrichmynarz/az-kviz"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.516"]
                 [reagent "0.8.1"]
                 [thi.ng/math "0.2.2"]
                 [thi.ng/color "1.3.0"]
                 [thi.ng/geom "1.0.0-RC3"]]
  :source-paths ["src"]
  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:dev"   ["trampoline" "run" "-m" "figwheel.main" "--build" "dev" "--repl"]
            "fig:test"  ["trampoline" "run" "-m" "figwheel.main" "--build" "test" "--serve"]} 
  :profiles {:dev {:clean-targets ^{:protect false} ["target"]
                   :dependencies [[binaryage/devtools "0.9.10"]
                                  [com.bhauman/cljs-test-display "0.1.1"]
                                  [com.bhauman/figwheel-main "0.2.0"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]
                                  [org.clojure/test.check "0.9.0"]]
                   :resource-paths ["target"]}})
