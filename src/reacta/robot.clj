(ns reacta.robot
  (:require [com.stuartsierra.component :as comp]
            [clojure.core.async :as a]))

(defrecord Robot [channels config]
  comp/Lifecycle
  (start [this]
    (if-not channels
      (let [ch (a/chan)
            channels {:to-reactors ch,
                      :reactors-pub (a/pub ch :type)
                      :from-reactors (a/chan 10)}]
        (assoc this :channels channels))
      this))
  (stop [this]
    (if channels
      (assoc this :channels nil))))

(defn new-robot [config]
  (map->Robot {:config config}))

(defn emit [robot event & more]
  (a/>!! (-> robot :channels :to-reactors) (apply array-map :type event more))
  nil)

(defn react [robot msg]
  (a/>!! (-> robot :channels :from-reactors) msg)
  nil)
