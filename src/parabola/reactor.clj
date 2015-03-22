(ns parabola.reactor
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as comp]
            [taoensso.timbre :as timbre]
            [parabola.robot :as robot]))

(defn wrap [robot proc]
  (fn [message]
    (when-not (= (:type message) ::stop)
      (let [res (proc message)]
        (cond (map? res)
              #_=> (robot/react robot res)
              (seq? res)
              #_=> (doseq [res res]
                     (robot/react robot res))))
      true)))

(defrecord Reactor [name robot ch proc]
  comp/Lifecycle
  (start [this]
    (timbre/info (str "reactor (" name ") started"))
    (a/tap (-> robot :channels :reactors-mult) ch)
    (a/go-loop []
      (let [msg (a/<! ch)]
        (timbre/debug "message received: " msg)
        (when ((wrap robot proc) msg)
          (recur))))
    this)
  (stop [this]
    (a/>!! ch {:type ::stop})
    (timbre/info (str "reactor (" name ") stopped"))
    this))
