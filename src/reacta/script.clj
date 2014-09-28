(ns reacta.script
  (:require [reacta.reactor :as r]))

(defn emit-reactor [event robot ch bindings body]
  `(r/->Reactor ~event ~robot ~ch (fn ~bindings ~@body)))

(defmacro defreactor [stimulus bindings & body]
  (let [robot (first bindings)
        ch (gensym 'ch)]
   `(defn ~(with-meta (gensym 'reactor) {:reactor true}) [~robot ~ch]
      ~(if (instance? java.util.regex.Pattern stimulus)
         (let [msg (gensym 'msg)]
           (emit-reactor :message robot ch [msg]
             `((let [[match# ~@(rest bindings)] (re-find ~stimulus (:content ~msg))]
                 (when-not (nil? match#)
                   ~@body)))))
         (emit-reactor stimulus robot ch (vec (rest bindings)) body)))))
