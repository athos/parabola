(ns reacta.adapters.twitter
  (:require [reacta.adapter :as adapter])
  (:import [twitter4j TwitterStream TwitterStreamFactory Status UserStreamListener]
           [twitter4j.conf Configuration ConfigurationBuilder]))

(defn make-config [credentials]
  (let [{:keys [consumer-key consumer-secret access-token access-token-secret]}
        credentials]
    (-> (ConfigurationBuilder.)
        (.setOAuthConsumerKey consumer-key)
        (.setOAuthConsumerSecret consumer-secret)
        (.setOAuthAccessToken access-token)
        (.setOAuthAccessTokenSecret access-token-secret)
        .build)))

(defn make-stream [credentials]
  (let [config (make-config credentials)]
    (.getInstance (TwitterStreamFactory. config))))

(defn make-listener []
  (reify UserStreamListener
    (onDeletionNotice [this _])
    (onScrubGeo [this _ _])
    (onStatus [this status]
      (println status))
    (onTrackLimitationNotice [this _])
    (onException [this _])
    (onBlock [this _ _])
    (onDeletionNotice [this _ _])
    (onDirectMessage [this _])
    (onFavorite [this _ _ _])
    (onFollow [this _ _])
    (onFriendList [this _])
    (onUnblock [this _ _])
    (onUnfavorite [this _ _ _])
    (onUnfollow [this _ _])
    (onUserListCreation [this _ _])
    (onUserListDeletion [this _ _])
    (onUserListMemberAddition [this _ _ _])
    (onUserListMemberDeletion [this _ _ _])
    (onUserListSubscription [this _ _ _])
    (onUserListUnsubscription [this _ _ _])
    (onUserListUpdate [this _ _])
    (onUserProfileUpdate [this _])))

(defn get-env [var-name]
  (get (System/getenv) var-name))

(defn twitter [robot]
  (let [stream (atom nil)]
    (reify
      adapter/Adapter
      (send [this msg]
        (println "sending:" (:content msg)))
      adapter/Lifecycle
      (start [this]
        (let [credentials {:consumer-key (get-env "TWITTER_CONSUMER_KEY")
                           :consumer-secret (get-env "TWITTER_CONSUMER_SECRET")
                           :access-token (get-env "TWITTER_ACCESS_TOKEN")
                           :access-token-secret (get-env "TWITTER_ACCESS_TOKEN_SECRET")}
              new-stream (make-stream credentials)
              listener (make-listener)]
          (reset! stream new-stream)
          (.addListener new-stream listener)
          (.user new-stream)))
      (stop [this]
        (.shutdown @stream)))))
