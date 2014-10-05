(ns reacta.main
  (:require [com.stuartsierra.component :as comp]
            [reacta.robot :as robot]
            [reacta.forwarder :as forward]
            [reacta.adapter-loader :as adapters]
            [reacta.script-loader :as scripts]))

(def ^:const ADAPTER_PREFIX "reacta.adapters")
(def ^:const SCRIPT_PREFIX "reacta.scripts")

(defn reacta-system []
  (comp/system-map
    :robot (robot/new-robot {:adapter-prefix ADAPTER_PREFIX
                             :script-prefix SCRIPT_PREFIX})
    :adapter-loader (comp/using
                      (adapters/new-adapter-loader #{:shell})
                      [:robot])
    :script-loader (comp/using
                     (scripts/new-script-loader)
                     [:robot])
    :forwarder (comp/using
                 (forward/new-forwarder :shell)
                 [:robot :adapter-loader])))

(defn run [system]
  (adapters/start-adapters (:adapter-loader system)))
