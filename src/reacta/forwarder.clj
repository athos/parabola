(ns reacta.forwarder
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as comp]
            [reacta.adapter :as adapter]))

(defrecord Forwarder [ch robot adapter-loader adapter-name]
  comp/Lifecycle
  (start [this]
    (if-not ch
      (let [ch (a/chan)]
        (a/go-loop []
          (let [[msg src] (a/alts! [ch (-> robot :channels :from-reactors)])]
            (when-not (= src ch)
              (adapter/send (get-in adapter-loader [:adapters adapter-name]) msg)
              (recur))))
        (assoc this :ch ch))
      this))
  (stop [this]
    (if ch
      (do (a/>!! ch ::stop)
          (assoc this :ch nil))
      this)))

(defn new-forwarder [adapter-name]
  (map->Forwarder {:adapter-name adapter-name}))
