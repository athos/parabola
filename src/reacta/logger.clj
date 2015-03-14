(ns reacta.logger
  (:require [com.stuartsierra.component :as comp]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]))

(defrecord Logger []
  comp/Lifecycle
  (start [this]
    (if (:dev env)
      (timbre/set-config!
       [:appenders :standard-out]
       {:min-level nil
        :enabled? true
        :fn (fn [{:keys [error? output]}]
              (binding [*out* (if error? *err* *out*)]
                (println output)))}))
    (timbre/info "started logging")
    this)
  (stop [this]
    (timbre/info "stopping logging ...")
    this))

(defn new-logger []
  (->Logger))
