(ns reacta.core
  (:require [clojure.core.async :as a]
            [reacta.platform :as p]
            [com.stuartsierra.component :as comp]
            [reacta.adapter :as adapter]
            [reacta.adapters.shell :as shell]
            reacta.scripts.hello))

(extend-protocol comp/Lifecycle
  reacta.adapter.Lifecycle
  (start [this] (adapter/start this))
  (stop [this] (adapter/stop this)))

(def adapter (shell/->ShellAdapter))

(defn run []
  (a/go-loop []
    (let [msg (a/<! p/from-reactors)]
      (adapter/send adapter msg)
      (recur)))
  (comp/start adapter))
