(ns reacta.platform
  (:require [clojure.core.async :as a]))

(def to-listeners (a/chan))
(def pub (a/pub to-listeners :type))

(defn emit [event & more]
  (a/>!! to-listeners (apply array-map :type event more))
  nil)

(defn receive [msg]
  (emit :message :content msg))

(def from-listeners (a/chan 10))

(defn react [msg]
  (a/>!! from-listeners msg))
