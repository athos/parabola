(ns reacta.scripts.hello
  (:require [reacta.script :refer [defreactor react]]
            [clojure.java.shell :refer [sh]]))

(defreactor :connected [robot message]
  (println "connected!"))

(defreactor #"hello" [robot]
  (react robot {:type :message :content "hello"}))

(defreactor #"time" [robot]
  (react robot {:type :message :content (clojure.string/trim-newline (:out (sh "date")))}))

(defreactor :close [robot message]
  (println "closed!"))
