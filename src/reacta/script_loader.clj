(ns reacta.script-loader
  (:require [clojure.core.async :as async]
            [bultitude.core :refer [namespaces-on-classpath]]
            [com.stuartsierra.component :as comp]))

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
         (reactor robot (async/chan 2)))))

(defn load-scripts [robot]
  (let [prefix (-> robot :config :script-prefix)]
    (vec (for [ns-name (namespaces-on-classpath :prefix prefix)]
           (do (require ns-name)
               (->Script ns-name (script-reactors robot ns-name)))))))

(defrecord ScriptLoader [robot scripts]
  comp/Lifecycle
  (start [this]
    (if-not scripts
      (let [scripts (load-scripts robot)]
        (doseq [script scripts]
          (comp/start script))
        (assoc this :scripts scripts))
      this))
  (stop [this]
    (if scripts
      (do (doseq [script scripts]
            (comp/stop script))
          (assoc this :scripts nil))
      this)))

(defn new-script-loader []
  (map->ScriptLoader {}))
