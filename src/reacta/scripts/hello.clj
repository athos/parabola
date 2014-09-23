(ns reacta.scripts.hello
  (:require [reacta.platform :refer [send]]
  	        [reacta.script :refer [on respond]]
  	        [clojure.java.shell :refer [sh]]))

(on :connected [_]
  (println "connected!"))

(respond #"hello"
  (send {:type :message :content "hello"}))

(respond #"time"
  (send {:type :message :content (clojure.string/trim-newline (:out (sh "date")))}))

(on :close [_]
  (println "closed!"))
