(ns reacta.core
  (:require [clojure.core.async :as a]
            [reacta.platform :as p]
            [com.stuartsierra.component :as comp]
            [bultitude.core :refer [namespaces-on-classpath]]
            [reacta.adapter :as adapter]))

(def ^:const ADAPTER_PREFIX "reacta.adapters")
(def ^:const SCRIPT_PREFIX "reacta.scripts")

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

(defrecord Script [ns reactors]
  comp/Lifecycle
  (start [this]
    (doseq [reactor reactors]
      (comp/start reactor))
    this)
  (stop [this]
    (doseq [reactor reactors]
      (comp/stop reactor))
    this))

(defn script-reactors [robot ns-name]
  (vec (for [reactor (filter (comp :reactor meta) (vals (ns-publics ns-name)))]
         (reactor robot (a/chan 2)))))

(defn load-scripts [robot]
  (->> (for [ns-name (namespaces-on-classpath :prefix SCRIPT_PREFIX)]
         (do (require ns-name)
             (->Script ns-name (script-reactors robot ns-name))))
       (into [])
       (assoc robot :scripts)))

(defn start-scripts [robot]
  (doseq [script (:scripts robot)]
    (comp/start script)))

(defn stop-scripts [robot]
  (doseq [script (:scripts robot)]
    (comp/stop script)))

(def robot (load-scripts {}))
(start-scripts robot)

(defn run []
  (a/go-loop []
    (let [msg (a/<! p/from-reactors)]
      (adapter/send adapter msg)
      (recur)))
  (comp/start adapter))
