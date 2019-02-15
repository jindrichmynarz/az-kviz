(defproject net.mynarz.az-kviz "0.1.0-SNAPSHOT"
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
  :plugins [[lein-cljsbuild "1.1.7"]]
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]
  :resource-paths ["public"]
  :figwheel {:http-server-root "."
             :nrepl-port 7002
             :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
             :css-dirs ["public/css"]}
  :cljsbuild {:builds {:app
                       {:source-paths ["src" "env/dev/cljs"]
                        :compiler
                        {:main "net.mynarz.az-kviz.dev"
                         :output-to "public/js/app.js"
                         :output-dir "public/js/out"
                         :asset-path "js/out"
                         :source-map true
                         :optimizations :none
                         :pretty-print true}
                        :figwheel
                        {:on-jsload "net.mynarz.az-kviz.core/mount-root"
                         :open-urls ["http://localhost:3449/index.html"]}}
                       :release
                       {:source-paths ["src" "env/prod/cljs"]
                        :compiler
                        {:output-to "public/js/app.js"
                         :output-dir "public/js/release"
                         :asset-path "js/out"
                         :optimizations :advanced
                         :infer-externs true
                         :pretty-print false}}
                       :test
                       {:source-paths ["src" "test"]
                        :compiler
                        {:asset-path "js/test"
                         :main net.mynarz.az-kviz.runner
                         :output-to "public/js/test.js"
                         :output-dir "public/js/test"
                         :optimizations :none
                         :pretty-print true}}}}
  :aliases {"package" ["do" "clean" ["cljsbuild" "once" "release"]]
            "test" ["doo" "chrome" "test"]}
  :profiles {:dev {:source-paths ["src" "env/dev/clj"]
                   :dependencies [[binaryage/devtools "0.9.10"]
                                  [figwheel-sidecar "0.5.18"]
                                  [nrepl "0.6.0"]
                                  [cider/piggieback "0.4.0"]
                                  [org.clojure/test.check "0.9.0"]]
                   :plugins [[lein-doo "0.1.10"]
                             [lein-figwheel "0.5.18"]
                             [lein-figwheel "0.5.18"]]}})
