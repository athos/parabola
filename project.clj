(defproject parabola "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/athos/parabola"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [com.stuartsierra/component "0.2.3"]
                 [bultitude "0.2.7"]
                 [org.twitter4j/twitter4j-core "4.0.3"]
                 [org.twitter4j/twitter4j-stream "4.0.3"]
                 [org.julienxx/clj-slack "0.4.1"]
                 [aleph "0.4.0"]
                 [manifold "0.1.0"]
                 [org.clojure/data.json "0.2.6"]
                 [environ "1.0.0"]
                 [com.taoensso/timbre "4.0.2"]]
  :profiles {:dev {:env {:dev true}
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]
                                  [com.cemerick/pomegranate "0.3.0"]]
                   :source-paths ["dev"]}}
  :global-vars {*warn-on-reflection* true})
