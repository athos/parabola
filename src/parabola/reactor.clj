(ns parabola.reactor
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as comp]
            [taoensso.timbre :as timbre]
            [parabola.robot :as robot]))

(defn wrap [robot name proc]
  (fn [msg]
    (when-not (= (:type msg) ::stop)
      (when-let [res (proc msg)]
        (timbre/debug (str "reactor (" name ") responded: " res))
        (cond (map? res)
              #_=> (robot/react robot (assoc res :message msg))
              (seq? res)
              #_=> (doseq [res res]
                     (robot/react robot (assoc res :message msg)))))
      true)))

(defrecord Reactor [name robot ch proc]
  comp/Lifecycle
  (start [this]
    (timbre/info (str "reactor (" name ") started"))
    (a/tap (-> robot :channels :reactors-mult) ch)
    (a/go-loop []
      (let [msg (a/<! ch)]
        (timbre/debug (str "reactor (" name ") received message: " msg))
        (when ((wrap robot name proc) msg)
          (recur))))
    this)
  (stop [this]
    (a/>!! ch {:type ::stop})
    (timbre/info (str "reactor (" name ") stopped"))
    this))
