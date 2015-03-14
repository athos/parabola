(ns reacta.adapter-loader
  (:require [reacta.adapter :as adapter]
            [com.stuartsierra.component :as comp]
            [taoensso.timbre :as timbre]))

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
      (let [adapters (->> (for [[name adapter] (load-adapters robot names)
                                :let [f (future (adapter/start adapter))]]
                            (do (timbre/debug (str "started thread for adapter " name))
                                [name {:adapter adapter :future f}]))
                          (into {}))]
        (assoc this :adapters adapters))
      this))
  (stop [this]
    (if adapters
      (do (doseq [[name {:keys [adapter future]}] adapters]
            (adapter/stop adapter)
            (future-cancel future)
            (timbre/debug (str "stopping thread for adapter " name)))
          (assoc this :adapters nil))
      this)))

(defn new-adapter-loader [names]
  (map->AdapterLoader {:names names}))
