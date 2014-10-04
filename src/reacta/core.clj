(ns reacta.core
  (:require [clojure.core.async :as a]
            [reacta.platform :as p]
            [com.stuartsierra.component :as comp]
            [reacta.adapter :as adapter]
            [reacta.adapter-loader :as adapters]
            [reacta.script-loader :as scripts]))

(def ^:const ADAPTER_PREFIX "reacta.adapters")
(def ^:const SCRIPT_PREFIX "reacta.scripts")

(def adapter-loader (atom (adapters/new-adapter-loader {} ADAPTER_PREFIX #{:shell})))

(def script-loader (scripts/new-script-loader {} SCRIPT_PREFIX))
(comp/start script-loader)

(defn run []
  (a/go-loop []
    (let [msg (a/<! p/from-reactors)]
      (adapter/send (:shell (:adapters @adapter-loader)) msg)
      (recur)))
  (swap! adapter-loader comp/start)
  (adapters/start-adapters @adapter-loader))
