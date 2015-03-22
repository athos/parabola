(ns parabola.script)

(defn reactors [& rs]
  (fn [msg]
    (some #(% msg) rs)))

(defmacro defreactors [name & reactors]
  `(def ~(with-meta name {:reactor true})
     (reactors ~@reactors)))

(defprotocol Response
  (respond [this msg]))

(defmacro message [re arg & body]
  `(fn [msg#]
     (when-let [m# (and (= (:type msg#) :message)
                        (re-matches ~re (:text msg#)))]
       (let [~arg (assoc msg# :matches m#)]
         (respond (do ~@body) msg#)))))

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
