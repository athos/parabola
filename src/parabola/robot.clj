(ns parabola.robot
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
      (assoc this :channels nil)
      this)))

(defn new-robot [config]
  (map->Robot {:config config}))

(defn emit [robot event msg]
  (a/>!! (-> robot :channels :to-reactors) (assoc msg :type event))
  nil)

(defn react [robot res]
  (a/>!! (-> robot :channels :from-reactors) res)
  nil)
