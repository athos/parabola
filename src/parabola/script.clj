(ns parabola.script
  (:require [parabola.reactor :as r]
            [parabola.robot :as robot]))

(defn emit-reactor [event robot ch bindings body]
  `(r/->Reactor ~event ~robot ~ch (fn ~bindings ~@body)))

(defmacro defreactor [stimulus bindings & body]
  (let [robot (first bindings)
        ch (gensym 'ch)]
   `(defn ~(with-meta (gensym 'reactor) {:reactor true}) [~robot ~ch]
      ~(if (instance? java.util.regex.Pattern stimulus)
         (let [msg (gensym 'msg)]
           (emit-reactor :message robot ch [msg]
             `((let [[match# ~@(rest bindings)] (re-find ~stimulus (:text ~msg))]
                 (when-not (nil? match#)
                   ~@body)))))
         (emit-reactor stimulus robot ch (vec (rest bindings)) body)))))

(defn react [robot msg]
  (robot/react robot msg))
