(ns user
  (:refer-clojure :exclude [send])
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer [pp pprint]]
            [clojure.core.async :as a]
            [clojure.java.shell :refer [sh]]))

(def to-listeners (a/chan))
(def pub (a/pub to-listeners :type))

(defn emit [event & more]
  (a/>!! to-listeners (apply array-map :type event more))
  nil)

(defn receive [msg]
  (emit :message :content msg))

(def from-listeners (a/chan 10))

(defn send [msg]
  (a/>!! from-listeners msg))

(defn print-prompt []
  (print "=> ")
  (flush))

(defn adapter-send [msg]
  (printf "\u001b[01;32m%s\u001b[0m\n" (:content msg))
  (print-prompt))

(defn adapter-start []
  (emit :connected)
  (print-prompt)
  (loop []
    (let [line (read-line)]
      (print-prompt)
      (when-not (or (nil? line) (= line "exit"))
        (receive line)
        (recur))))
  (emit :close))

(defn run []
  (a/go-loop []
    (let [msg (a/<! from-listeners)]
      (adapter-send msg)
      (recur)))
  (adapter-start))

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

(on :connected [_]
  (println "connected!"))

(respond #"hello"
  (send {:type :message :content "hello"}))

(respond #"time"
  (send {:type :message :content (clojure.string/trim-newline (:out (sh "date")))}))

(on :close [_]
  (println "closed!"))
