(ns parabola.response)

(defprotocol Response
  (respond [this msg]))

(extend-protocol Response
  Object
  (respond [this _] this)
  String
  (respond [content _]
    {:type :message
     :content content})
  clojure.lang.Fn
  (respond [f msg]
    (respond (f msg) msg)))
