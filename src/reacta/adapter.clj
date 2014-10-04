(ns reacta.adapter
  (:refer-clojure :exclude [send])
  (:require [reacta.communication :as comm]))

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
  (apply comm/emit event more))

(defn receive [msg]
  (comm/emit :message :content msg))
