(defproject reacta "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/athos/reacta"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [com.stuartsierra/component "0.2.2"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.6"]
                                  [com.cemerick/pomegranate "0.3.0"]]
                   :source-paths ["dev"]}}
  :global-vars {*warn-on-reflection* true})
