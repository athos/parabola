(ns parabola.forwarder
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as comp]
            [parabola.adapter :as adapter]
            [taoensso.timbre :as timbre]))

(defrecord Forwarder [ch robot adapter-loader]
  comp/Lifecycle
  (start [this]
    (if-not ch
      (let [ch (a/chan)]
        (timbre/info "forwarder started")
        (a/go-loop []
          (let [[res src] (a/alts! [ch (-> robot :channels :from-reactors)])]
            (when-not (= src ch)
              (let [adapter (or (:adapter res)
                                (get-in res [:message :adapter]))]
                (timbre/debug (str "response forwarded to " adapter ": " res))
                (adapter/send (get-in adapter-loader [:adapters adapter :adapter]) res)
                (recur)))))
        (assoc this :ch ch))
      this))
  (stop [this]
    (if ch
      (do (a/>!! ch ::stop)
          (timbre/info "forwarder stopped")
          (assoc this :ch nil))
      this)))

(defn new-forwarder []
  (map->Forwarder {}))
