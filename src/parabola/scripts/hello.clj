(ns parabola.scripts.hello
  (:require [parabola.script :refer [defreactors message]]
            [clojure.java.shell :refer [sh]]
            [taoensso.timbre :as timbre]))

(defn ^:reactor log-connected [msg]
  (when (= (:type msg) :connected)
    (timbre/info "connected!")))

(defreactors example-reactors
  (message #"ping" []
    "ping")
  (message #"time" []
    (clojure.string/trim-newline (:out (sh "date"))))
  (message #"hello" {{:keys [name]} :user}
    (str "@" name " hello")))

(defn ^:reactor log-closed [msg]
  (when (= (:type msg) :close)
    (timbre/info "closed!")))
