(ns reacta.platform
  (:require [clojure.core.async :as a]))

(def to-reactors (a/chan))
(def pub (a/pub to-reactors :type))

(defn emit [event & more]
  (a/>!! to-reactors (apply array-map :type event more))
  nil)

(def from-reactors (a/chan 10))

(defn react [msg]
  (a/>!! from-reactors msg))
