(ns reacta.adapter
  (:refer-clojure :exclude [send])
  (:require [reacta.robot :as robot]))

(defprotocol Adapter
  (send [this msg])
  (reply [this msg])
  (emote [this msg])
  (topic [this msg])
  (play [this msg]))

(defprotocol Lifecycle
  (start [this])
  (stop [this]))

(defn emit [robot event & more]
  (apply robot/emit robot event more))

(defn receive [robot msg]
  (robot/emit robot :message :content msg))
