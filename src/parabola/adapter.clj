(ns parabola.adapter
  (:refer-clojure :exclude [send])
  (:require [parabola.robot :as robot]))

(defprotocol Adapter
  (send [this msg])
  (reply [this msg])
  (emote [this msg])
  (topic [this msg])
  (play [this msg]))

(defprotocol Lifecycle
  (init [this])
  (start [this])
  (stop [this]))

(defn emit [robot event msg]
  (robot/emit robot event msg))

(defn receive [robot msg]
  (emit robot :message msg))
