(ns parabola.main
  (:require [com.stuartsierra.component :as comp]
            [parabola.robot :as robot]
            [parabola.logger :as logger]
            [parabola.forwarder :as forward]
            [parabola.adapter-loader :as adapters]
            [parabola.script-loader :as scripts]))

(def ^:const ADAPTER_PREFIX "parabola.adapters")
(def ^:const SCRIPT_PREFIX "parabola.scripts")

(defn parabola-system []
  (comp/system-map
    :logger (logger/new-logger)
    :robot (comp/using
             (robot/new-robot {:adapter-prefix ADAPTER_PREFIX
                               :script-prefix SCRIPT_PREFIX})
             [:logger])
    :adapter-loader (comp/using
                      (adapters/new-adapter-loader)
                      [:robot :logger])
    :script-loader (comp/using
                     (scripts/new-script-loader)
                     [:robot :logger])
    :forwarder (comp/using
                 (forward/new-forwarder :slack)
                 [:robot :logger :adapter-loader])))
