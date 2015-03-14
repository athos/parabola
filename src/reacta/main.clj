(ns reacta.main
  (:require [com.stuartsierra.component :as comp]
            [reacta.robot :as robot]
            [reacta.logger :as logger]
            [reacta.forwarder :as forward]
            [reacta.adapter-loader :as adapters]
            [reacta.script-loader :as scripts]))

(def ^:const ADAPTER_PREFIX "reacta.adapters")
(def ^:const SCRIPT_PREFIX "reacta.scripts")

(defn reacta-system []
  (comp/system-map
    :logger (logger/new-logger)
    :robot (comp/using
             (robot/new-robot {:adapter-prefix ADAPTER_PREFIX
                               :script-prefix SCRIPT_PREFIX})
             [:logger])
    :adapter-loader (comp/using
                      (adapters/new-adapter-loader #{:slack})
                      [:robot :logger])
    :script-loader (comp/using
                     (scripts/new-script-loader)
                     [:robot :logger])
    :forwarder (comp/using
                 (forward/new-forwarder :slack)
                 [:robot :logger :adapter-loader])))

