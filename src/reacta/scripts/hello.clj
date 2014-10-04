(ns reacta.scripts.hello
  (:require [reacta.communication :refer [react]]
            [reacta.script :refer [defreactor]]
            [clojure.java.shell :refer [sh]]))

(defreactor :connected [robot message]
  (println "connected!"))

(defreactor #"hello" [robot]
  (react {:type :message :content "hello"}))

(defreactor #"time" [robot]
  (react {:type :message :content (clojure.string/trim-newline (:out (sh "date")))}))

(defreactor :close [robot message]
  (println "closed!"))
