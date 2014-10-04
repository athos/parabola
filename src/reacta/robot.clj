(ns reacta.robot
  (:require [com.stuartsierra.component :as comp]))

(defrecord Robot [comm config]
  comp/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn new-robot [config]
  (map->Robot {:config config}))
