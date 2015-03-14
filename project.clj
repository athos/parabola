(defproject reacta "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/athos/reacta"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [com.stuartsierra/component "0.2.2"]
                 [bultitude "0.2.6"]
                 [org.twitter4j/twitter4j-core "4.0.2"]
                 [org.twitter4j/twitter4j-stream "4.0.2"]
                 [org.julienxx/clj-slack "0.4.1"]
                 [aleph "0.4.0-beta3"]
                 [manifold "0.1.0-beta11"]
                 [org.clojure/data.json "0.2.5"]
                 [environ "1.0.0"]
                 [com.taoensso/timbre "3.3.1"]]
  :profiles {:dev {:env {:dev true}
                   :dependencies [[org.clojure/tools.namespace "0.2.6"]
                                  [com.cemerick/pomegranate "0.3.0"]]
                   :source-paths ["dev"]}}
  :global-vars {*warn-on-reflection* true})
