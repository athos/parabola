(ns reacta.adapter-loader
  (:require [reacta.adapter :as adapter]
            [com.stuartsierra.component :as comp]))

(defn symbol-concat [& syms]
  (symbol (apply str syms)))

(defn load-adapter [robot prefix adapter-name]
  (let [name (if (instance? clojure.lang.Named adapter-name)
               (name adapter-name)
               (str adapter-name))
        ns-name (symbol-concat prefix '. name)]
    (require ns-name)
    (when-let [v (find-var (symbol-concat ns-name '/ name))]
      (@v robot))))

(defn load-adapters [robot prefix names]
  (->> (for [name names]
         [name (load-adapter robot prefix name)])
       (into {})))

(defrecord AdapterLoader [robot prefix names adapters]
  comp/Lifecycle
  (start [this]
    (if-not adapters
      (assoc this :adapters (load-adapters robot prefix names))
      this))
  (stop [this]
    (if adapters
      (do (doseq [adapter (vals adapters)]
            (adapter/stop adapter))
          (assoc this :adapters nil))
      this)))

(defn new-adapter-loader [prefix names]
  (map->AdapterLoader {:prefix prefix :names names}))

(defn start-adapters [loader]
  (doseq [[_ adapter] (:adapters loader)]
    (adapter/start adapter)))
