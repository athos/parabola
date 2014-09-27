(ns reacta.core
  (:require [clojure.core.async :as a]
            [reacta.platform :as p]
            [com.stuartsierra.component :as comp]
            [bultitude.core :refer [namespaces-on-classpath]]
            [reacta.adapter :as adapter]
            reacta.scripts.hello))

(def ^:const ADAPTER_PREFIX "reacta.adapters")

(defn symbol-concat [& syms]
  (symbol (apply str syms)))

(defn load-adapter [name]
  (let [ns-name (symbol-concat ADAPTER_PREFIX '. name)]
    (require ns-name)
    (when-let [v (find-var (symbol-concat ns-name '/ name))]
      (@v))))

(extend-protocol comp/Lifecycle
  reacta.adapter.Lifecycle
  (start [this] (adapter/start this))
  (stop [this] (adapter/stop this)))

(def adapter (load-adapter "shell"))

(defn run []
  (a/go-loop []
    (let [msg (a/<! p/from-reactors)]
      (adapter/send adapter msg)
      (recur)))
  (comp/start adapter))
