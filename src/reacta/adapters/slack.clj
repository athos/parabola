(ns reacta.adapters.slack
  (:require [environ.core :refer [env]]
            [reacta.adapter :as adapter]
            [clj-slack.rtm :as rtm]
            [aleph.http :as http]
            [manifold.stream :as s]
            [clojure.data.json :as json]))

(def slack-api-url "https://slack.com/api")
(def slack-api-token (env :slack-api-token))
(def slack-connection {:api-url slack-api-url :token slack-api-token})

(defrecord SlackAdapter [robot stream users closed?]
  adapter/Adapter
  (send [this msg]
    (println "seinding message:" msg))
  adapter/Lifecycle
  (init [this]
    (let [{:keys [url users]} (rtm/start slack-connection)
          users (reduce (fn [m {:keys [id name]}] (assoc m id name)) {} users)
          stream @(http/websocket-client url)]
      (assoc this :stream stream :users users :closed? (atom false))))
  (start [this]
    (reset! closed? false)
    (loop []
      (when-not @closed?
        (let [v @(s/try-take! (s/->source stream) 10000)]
          (if v
            (let [json (json/read-str v :key-fn keyword)]
              (case (:type json)
                "message"
                #_=> (do (prn "received message:" (:text json))
                         (adapter/receive robot (:text json)))
                nil)
              (recur))
            (recur))))))
  (stop [this]
    (reset! closed? true)
    (s/close! stream)
    (assoc this :stream nil)))

(defn slack [robot]
  (map->SlackAdapter {:robot robot}))
