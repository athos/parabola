(ns reacta.script
  (:require [reacta.platform :as p]))

(defmacro on [type arg & body]
  `(p/add-reactor! ~type (fn ~arg ~@body)))

(defmacro respond [bindings & body]
  (let [regex? (instance? java.util.regex.Pattern bindings)
        names (if regex? [] (first bindings))
        regex (if regex? bindings (second bindings))]
   `(on :message [msg#]
      (let [[match# ~@names] (re-find ~regex (:content msg#))]
        (when-not (nil? match#)
          ~@body)))))
