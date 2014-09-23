(ns reacta.adapters.shell
  (:require [reacta.platform :as p]))

(defn print-prompt []
  (print "=> ")
  (flush))

(defn adapter-send [msg]
  (printf "\u001b[01;32m%s\u001b[0m\n" (:content msg))
  (print-prompt))

(defn adapter-start []
  (p/emit :connected)
  (print-prompt)
  (loop []
    (let [line (read-line)]
      (print-prompt)
      (when-not (or (nil? line) (= line "exit"))
        (p/receive line)
        (recur))))
  (p/emit :close))
