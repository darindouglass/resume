(defproject resume "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.773"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.11.21"]
                 [reagent "0.10.0"]
                 [re-frame "1.2.0"]
                 [day8.re-frame/tracing "0.6.0"]
                 [garden "1.3.10"]
                 [ns-tracker "0.4.0"]]
  :plugins [[cider/cider-nrepl "0.25.6"]
            [lein-shadow "0.3.1"]
            [lein-garden "0.3.0"]
            [lein-shell "0.5.0"]]
  :min-lein-version "2.9.0"
  :jvm-opts ["-Xmx1G"]
  :source-paths ["src/clj" "src/cljs"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "resources/public/css"]
  :garden {:builds [{:id "screen"
                     :source-paths ["src/clj"]
                     :stylesheet resume.css/screen
                     :compiler {:output-to "screen.css"
                                :pretty-print? true}}]}
  :shadow-cljs {:nrepl {:port 8777}
                :builds {:app {:target :browser
                               :output-dir "."
                               :asset-path "."
                               :modules {:app {:init-fn resume.core/init
                                               :preloads [devtools.preload
                                                          day8.re-frame-10x.preload]}}
                               :dev {:compiler-options {:closure-defines {re-frame.trace.trace-enabled? true
                                                                          day8.re-frame.tracing.trace-enabled? true}}}
                               :release {:build-options
                                         {:ns-aliases
                                          {day8.re-frame.tracing day8.re-frame.tracing-stubs}}}
                               :devtools {:http-root "."
                                          :http-port 8280}}}}
  :shell {:commands {"karma" {:windows ["cmd" "/c" "karma"]
                              :default-command "karma"}
                     "open"  {:windows ["cmd" "/c" "start"]
                              :macosx "open"
                              :linux "xdg-open"}}}
  :aliases {"watch" ["with-profile" "dev" "do"
                     ["shadow" "watch" "app" "browser-test" "karma-test"]]
            "release" ["with-profile" "prod" "do"
                       ["shadow" "release" "app"]]
            "build-report" ["with-profile" "prod" "do"
                            ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]
            "ci" ["with-profile" "prod" "do"
                  ["shadow" "compile" "karma-test"]
                  ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}
  :profiles {:dev
             {:dependencies [[binaryage/devtools "1.0.2"]
                             [day8.re-frame/re-frame-10x "0.7.0"
                              :exclusions [cljsjs/react-highlight]]]
              :source-paths ["dev"]}}
  :prep-tasks [["garden" "once"]])
