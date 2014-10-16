(ns reacta.adapters.twitter
  (:require [reacta.adapter :as adapter])
  (:import [twitter4j TwitterFactory TwitterStream TwitterStreamFactory Status UserStreamListener]
           [twitter4j.conf Configuration ConfigurationBuilder]))

(defn make-config [consumer-key consumer-secret access-token access-token-secret]
  (-> (ConfigurationBuilder.)
      (.setOAuthConsumerKey consumer-key)
      (.setOAuthConsumerSecret consumer-secret)
      (.setOAuthAccessToken access-token)
      (.setOAuthAccessTokenSecret access-token-secret)
      .build))

(defn make-listener [robot]
  (reify UserStreamListener
    (onDeletionNotice [this _])
    (onScrubGeo [this _ _])
    (^void onStatus [this ^Status status]
      (adapter/receive robot (.getText status))
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
  (let [stream (ref nil)
        twitter (ref nil)]
    (reify
      adapter/Adapter
      (send [this msg]
        (println "sending:" (:content msg))
        (.updateStatus @twitter (:content msg)))
      adapter/Lifecycle
      (start [this]
        (let [config (make-config (get-env "TWITTER_CONSUMER_KEY")
                                  (get-env "TWITTER_CONSUMER_SECRET")
                                  (get-env "TWITTER_ACCESS_TOKEN")
                                  (get-env "TWITTER_ACCESS_TOKEN_SECRET"))
              new-stream (identity (.getInstance (TwitterStreamFactory. config)))
              listener (make-listener robot)
              new-twitter (.getInstance (TwitterFactory. config))]
          (dosync
            (ref-set stream new-stream)
            (ref-set twitter new-twitter))
          (.addListener new-stream listener)
          (.user new-stream)))
      (stop [this]
        (.shutdown @stream)
        (dosync
          (ref-set twitter nil)
          (ref-set stream nil))))))
