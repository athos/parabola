(ns parabola.scripts.hello
  (:require [parabola
             [script :refer :all]
             [middleware :refer [forwarding-to only-when]]]
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

(def ^:reactor forwarding-reactors
  (-> (reactors
        (message #"twitter (.+)$" {[_ content] :matches}
          content))
      (forwarding-to :twitter)))

(def ^:reactor twitter-reactors
  (-> (context #"@parabola_test " []
        (message #"ping" {{:keys [name]} :user}
          (str "@" name " pong"))
        (message #"hello" {{:keys [name]} :user}
          (str "@" name " hello")))
      (only-when #(= (:adapter %) :twitter))))

(defn ^:reactor log-closed [msg]
  (when (= (:type msg) :close)
    (timbre/info "closed!")))
