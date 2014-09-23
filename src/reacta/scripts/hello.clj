(ns reacta.scripts.hello
  (:require [reacta.platform :refer [react]]
  	        [reacta.script :refer [on respond]]
  	        [clojure.java.shell :refer [sh]]))

(on :connected [_]
  (println "connected!"))

(respond #"hello"
  (react {:type :message :content "hello"}))

(respond #"time"
  (react {:type :message :content (clojure.string/trim-newline (:out (sh "date")))}))

(on :close [_]
  (println "closed!"))
