(ns reacta.core
  (:require [clojure.core.async :as a]
            [reacta.platform :as p]
            [reacta.adapters.shell :as shell]
            reacta.scripts.hello))

(defn run []
  (a/go-loop []
    (let [msg (a/<! p/from-reactors)]
      (shell/adapter-send msg)
      (recur)))
  (shell/adapter-start))
