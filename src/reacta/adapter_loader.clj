(ns reacta.adapter-loader
  (:require [reacta.adapter :as adapter]
            [com.stuartsierra.component :as comp]))

(defn symbol-concat [& syms]
  (symbol (apply str syms)))

(defn load-adapter [prefix adapter-name]
  (let [name (if (instance? clojure.lang.Named adapter-name)
               (name adapter-name)
               (str adapter-name))
        ns-name (symbol-concat prefix '. name)]
    (require ns-name)
    (when-let [v (find-var (symbol-concat ns-name '/ name))]
      (@v))))

(defn load-adapters [prefix names]
  (->> (for [name names]
         [name (load-adapter prefix name)])
       (into {})))

(defrecord AdapterLoader [robot prefix names adapters]
  comp/Lifecycle
  (start [this]
    (if-not adapters
      (assoc this :adapters (load-adapters prefix names))
      this))
  (stop [this]
    (if adapters
      (do (doseq [adapter (vals adapters)]
            (adapter/stop adapter))
          (assoc this :adapters nil))
      this)))

(defn new-adapter-loader [robot prefix names]
  (map->AdapterLoader {:robot robot :prefix prefix :names names}))

(defn start-adapters [loader]
  (doseq [[_ adapter] (:adapters loader)]
    (adapter/start adapter)))
