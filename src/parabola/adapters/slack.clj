(ns parabola.adapters.slack
  (:require [environ.core :refer [env]]
            [parabola.adapter :as adapter]
            [clj-slack.rtm :as rtm]
            [aleph.http :as http]
            [manifold.stream :as s]
            [clojure.data.json :as json]
            [taoensso.timbre :as timbre]))

(def slack-api-url "https://slack.com/api")
(def slack-api-token (env :slack-api-token))
(def slack-connection {:api-url slack-api-url :token slack-api-token})

(defn- extract [src]
  (let [m (reduce (fn [m {:keys [id name]}] (assoc m id name)) {} src)]
    {:id->name m
     :name->id (zipmap (vals m) (keys m))}))

(defrecord SlackAdapter [robot stream channels users closed?]
  adapter/Adapter
  (send [this msg]
    (when-not @closed?
      (case (:type msg)
        :message
        #_=> (let [json {:id (rand-int Integer/MAX_VALUE)
                         :type :message
                         :channel (get-in channels [:name->id "random"])
                         :text (:content msg)}]
               (println "seinding message:" json)
               (s/try-put! (s/->sink stream) (json/write-str json) 10000))
        nil)))
  adapter/Lifecycle
  (init [this]
    (let [{:keys [url channels users]} (rtm/start slack-connection)
          chans (extract channels)
          users (extract users)
          stream @(http/websocket-client url)]
      (timbre/debug "init slack adapter")
      (assoc this :stream stream :channels chans :users users :closed? (atom false))))
  (start [this]
    (reset! closed? false)
    (timbre/debug "starting slack adapter ...")
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
    (timbre/debug "stopped slack adapter")
    (assoc this :stream nil)))

(defn slack [robot]
  (map->SlackAdapter {:robot robot}))
