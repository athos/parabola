(ns parabola.adapters.twitter
  (:require [environ.core :refer [env]]
            [parabola.adapter :as adapter]
            [taoensso.timbre :as timbre])
  (:import [twitter4j TwitterFactory Twitter TwitterStream TwitterStreamFactory Status UserStreamListener]
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
      (timbre/info (str "message received: " (.getText status))))
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

(defrecord TwitterAdapter [robot ^Twitter twitter ^TwitterStream stream]
  adapter/Adapter
  (send [this msg]
    (.updateStatus twitter ^String (:content msg))
    (timbre/info (str "message sent: " msg)))
  adapter/Lifecycle
  (init [this]
    (let [^Configuration config (make-config (env :twitter-consumer-key)
                                             (env :twitter-consumer-secret)
                                             (env :twitter-access-token)
                                             (env :twitter-access-token-secret))
          stream (identity (.getInstance (TwitterStreamFactory. config)))
          listener (make-listener robot)
          twitter (.getInstance (TwitterFactory. config))]
      (.addListener stream listener)
      (timbre/debug "init twitter adapter")
      (assoc this :twitter twitter :stream stream)))
  (start [this]
    (timbre/debug "starting twitter adapter ...")
    (.user stream))
  (stop [this]
    (.shutdown stream)
    (timbre/debug "stopped twitter adapter")
    (assoc this :stream nil :twitter nil)))

(defn twitter [robot]
  (map->TwitterAdapter {:robot robot}))
