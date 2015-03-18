(ns parabola.adapter-loader
  (:require [parabola.adapter :as adapter]
            [com.stuartsierra.component :as comp]
            [bultitude.core :as bult]
            [taoensso.timbre :as timbre]
            [clojure.string :as str]))

(defn symbol-concat [& syms]
  (symbol (apply str syms)))

(defn load-adapter [robot adapter-ns adapter-name]
  (require adapter-ns)
  (when-let [v (find-var (symbol-concat adapter-ns '/ adapter-name))]
    (adapter/init (@v robot))))

(defn load-adapters [robot]
  (let [adapter-prefix (get-in robot [:config :adapter-prefix])]
    (->> (for [adapter-ns (bult/namespaces-on-classpath :prefix adapter-prefix)
               :let [adapter-name (-> (str adapter-ns) (str/split #"[.]") peek)
                     adapter (load-adapter robot adapter-ns adapter-name)]
               :when adapter]
           [(keyword adapter-name) adapter])
         (into {}))))

(defrecord AdapterLoader [robot adapters]
  comp/Lifecycle
  (start [this]
    (if-not adapters
      (let [adapters (->> (for [[adapter-name adapter] (load-adapters robot)
                                :let [f (future (adapter/start adapter))]]
                            (do (timbre/debug (str "started thread for adapter "
                                                   (name adapter-name)))
                                [adapter-name {:adapter adapter :future f}]))
                          (into {}))]
        (assoc this :adapters adapters))
      this))
  (stop [this]
    (if adapters
      (do (doseq [[adapter-name {:keys [adapter future]}] adapters]
            (adapter/stop adapter)
            (future-cancel future)
            (timbre/debug (str "stopping thread for adapter " (name adapter-name))))
          (assoc this :adapters nil))
      this)))

(defn new-adapter-loader []
  (map->AdapterLoader {}))
