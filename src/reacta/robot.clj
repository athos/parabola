(ns reacta.robot
  (:require [com.stuartsierra.component :as comp]
            [clojure.core.async :as a]
            [taoensso.timbre :as timbre]))

(defrecord Robot [channels config]
  comp/Lifecycle
  (start [this]
    (if-not channels
      (let [ch (a/chan)
            channels {:to-reactors ch,
                      :reactors-pub (a/pub ch :type)
                      :from-reactors (a/chan 10)}]
        (timbre/info "started robot")
        (assoc this :channels channels))
      this))
  (stop [this]
    (timbre/info "stopped robot")
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
