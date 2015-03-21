(ns parabola.scripts.hello
  (:require [parabola.script :refer [defreactor react]]
            [clojure.java.shell :refer [sh]]
            [taoensso.timbre :as timbre]))

(defreactor :connected [robot message]
  (timbre/info "connected!"))

(defreactor #"@reacta_test ping" [robot]
  (react robot {:type :message :content "pong"}))

(defreactor #"hello" [robot]
  (react robot {:type :message :content "hello"}))

(defreactor #"time" [robot]
  (react robot {:type :message :content (clojure.string/trim-newline (:out (sh "date")))}))

(defreactor :close [robot message]
  (timbre/info "closed!"))
