(ns reacta.adapters.shell
  (:require [reacta.adapter :as adapter]))

(defn print-prompt []
  (print "=> ")
  (flush))

(defrecord ShellAdapter []
  adapter/Adapter
  (send [this msg]
    (printf "\u001b[01;32m%s\u001b[0m\n" (:content msg))
    (print-prompt))

  adapter/Lifecycle
  (start [this]
    (adapter/emit :connected)
    (print-prompt)
    (loop []
      (let [line (read-line)]
        (print-prompt)
        (when-not (or (nil? line) (= line "exit"))
          (adapter/receive line)
          (recur))))
    (adapter/emit :close)))
