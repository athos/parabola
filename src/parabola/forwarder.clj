(ns parabola.forwarder
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as comp]
            [parabola.adapter :as adapter]
            [taoensso.timbre :as timbre]))

(defrecord Forwarder [ch robot adapter-loader adapter-name]
  comp/Lifecycle
  (start [this]
    (if-not ch
      (let [ch (a/chan)]
        (a/go-loop []
          (timbre/info "forwarder started")
          (let [[msg src] (a/alts! [ch (-> robot :channels :from-reactors)])]
            (when-not (= src ch)
              (timbre/debug (str "message forwarded: " msg))
              (adapter/send (get-in adapter-loader [:adapters adapter-name :adapter]) msg)
              (recur))))
        (assoc this :ch ch))
      this))
  (stop [this]
    (if ch
      (do (a/>!! ch ::stop)
          (timbre/info "forwarder stopped")
          (assoc this :ch nil))
      this)))

(defn new-forwarder [adapter-name]
  (map->Forwarder {:adapter-name adapter-name}))
