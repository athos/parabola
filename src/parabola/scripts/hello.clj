(ns parabola.scripts.hello
  (:require [parabola.script :refer [defreactor react]]
            [clojure.java.shell :refer [sh]]
            [taoensso.timbre :as timbre]))

(defn ^:reactor log-connected [msg]
  (when (= (:type msg) :connected)
    (timbre/info "connected!")))

(defn ^:reactor respond-ping [msg]
  (when (and (= (:type msg) :message)
             (re-matches #"ping" (:text msg)))
    {:type :message
     :content "pong"
     :message msg}))

(defn ^:reactor respond-hello [msg]
  (when (and (= (:type msg) :message)
             (re-matches #"hello" (:text msg)))
    {:type :message
     :content "hello"
     :message msg}))

(defn ^:reactor respond-time [msg]
  (when (and (= (:type msg) :message)
             (re-matches #"time" (:text msg)))
    (let [content (clojure.string/trim-newline (:out (sh "date")))]
      {:type :message
       :content content
       :message msg})))

(defn ^:reactor log-closed [msg]
  (when (= (:type msg) :close)
    (timbre/info "closed!")))
