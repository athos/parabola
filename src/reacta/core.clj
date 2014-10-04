(ns reacta.core
  (:require [clojure.core.async :as a]
            [reacta.communication :as comm]
            [com.stuartsierra.component :as comp]
            [reacta.adapter :as adapter]
            [reacta.adapter-loader :as adapters]
            [reacta.script-loader :as scripts]))

(def ^:const ADAPTER_PREFIX "reacta.adapters")
(def ^:const SCRIPT_PREFIX "reacta.scripts")

(defn reacta-system []
  (comp/system-map
    :robot {}
    :adapter-loader (comp/using
                      (adapters/new-adapter-loader ADAPTER_PREFIX #{:shell})
                      [:robot])
    :script-loader (comp/using
                     (scripts/new-script-loader SCRIPT_PREFIX)
                     [:robot])))

(defn run [system]
  (let [{:keys [adapter-loader]} system]
    (a/go-loop []
      (let [msg (a/<! comm/from-reactors)]
        (adapter/send (:shell (:adapters adapter-loader)) msg)
        (recur)))
    (adapters/start-adapters adapter-loader)))
