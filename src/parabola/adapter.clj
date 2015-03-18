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

(defn emit [robot event & more]
  (apply robot/emit robot event more))

(defn receive [robot msg]
  (robot/emit robot :message :content msg))
