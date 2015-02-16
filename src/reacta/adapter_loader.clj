(ns reacta.adapter-loader
  (:require [reacta.adapter :as adapter]
            [com.stuartsierra.component :as comp]))

(defn symbol-concat [& syms]
  (symbol (apply str syms)))

(defn load-adapter [robot adapter-name]
  (let [name (if (instance? clojure.lang.Named adapter-name)
               (name adapter-name)
               (str adapter-name))
        prefix (-> robot :config :adapter-prefix)
        ns-name (symbol-concat prefix '. name)]
    (require ns-name)
    (when-let [v (find-var (symbol-concat ns-name '/ name))]
      (adapter/init (@v robot)))))

(defn load-adapters [robot names]
  (->> (for [name names]
         [name (load-adapter robot name)])
       (into {})))

(defrecord AdapterLoader [robot names adapters]
  comp/Lifecycle
  (start [this]
    (if-not adapters
      (assoc this :adapters (load-adapters robot names))
      this))
  (stop [this]
    (if adapters
      (do (doseq [adapter (vals adapters)]
            (adapter/stop adapter))
          (assoc this :adapters nil))
      this)))

(defn new-adapter-loader [names]
  (map->AdapterLoader {:names names}))

(defn start-adapters [loader]
  (doseq [[_ adapter] (:adapters loader)]
    (adapter/start adapter)))
