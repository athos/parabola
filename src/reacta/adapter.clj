(ns reacta.adapter
  (:refer-clojure :exclude [send])
  (:require [reacta.platform :as p]))

(defprotocol Adapter
  (send [this msg])
  (reply [this msg])
  (emote [this msg])
  (topic [this msg])
  (play [this msg]))

(defprotocol Lifecycle
  (start [this])
  (stop [this]))

(defn emit [event & more]
  (apply p/emit event more))

(defn receive [msg]
  (p/emit :message :content msg))
