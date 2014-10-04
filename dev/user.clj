(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer [pp pprint]]
            [reacta.core :refer :all]
            [com.stuartsierra.component :as comp]))

(def system nil)

(defn init []
  (alter-var-root #'system (constantly (reacta-system))))

(defn start []
  (alter-var-root #'system comp/start))

(defn stop []
  (alter-var-root #'system comp/stop))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
