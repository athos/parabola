(ns parabola.scripts.hello
  (:require [parabola.script :refer [defreactors reactors message]]
            [clojure.java.shell :refer [sh]]
            [taoensso.timbre :as timbre]))

(defn ^:reactor log-connected [msg]
  (when (= (:type msg) :connected)
    (timbre/info "connected!")))

(defreactors example-reactors
  (message #"ping" []
    "ping")
  (message #"time" []
    (clojure.string/trim-newline (:out (sh "date"))))
  (message #"hello" {{:keys [name]} :user}
    (str "@" name " hello")))

(defn wrap-forwarding-to-twitter [reactors]
  (fn [msg]
    (let [res (reactors msg)]
      (assoc res :adapter :twitter))))

(def ^:reactor forwarding-reactors
  (-> (reactors
        (message #"twitter (.+)$" {[_ content] :matches}
          content))
      wrap-forwarding-to-twitter))

(defn ^:reactor log-closed [msg]
  (when (= (:type msg) :close)
    (timbre/info "closed!")))
