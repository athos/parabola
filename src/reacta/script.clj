(ns reacta.script
  (:require [clojure.core.async :as a]
            [reacta.platform :refer [pub]]))

(defmacro on [type arg & body]
  `(let [ch# (a/chan 10)]
     (a/sub pub ~type ch#)
     (a/go-loop []
       (let [v# (a/<! ch#)]
         ((fn ~arg ~@body) v#)
         (recur)))))

(defmacro respond [bindings & body]
  (let [regex? (instance? java.util.regex.Pattern bindings)
        names (if regex? [] (first bindings))
        regex (if regex? bindings (second bindings))]
   `(on :message [msg#]
      (let [[match# ~@names] (re-find ~regex (:content msg#))]
        (when-not (nil? match#)
          ~@body)))))
