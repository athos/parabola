(ns reacta.adapters.shell
  (:require [reacta.adapter :as adapter]))

(defn print-prompt []
  (print "=> ")
  (flush))

(defrecord ShellAdapter [robot]
  adapter/Adapter
  (send [this msg]
    (printf "\u001b[01;32m%s\u001b[0m\n" (:content msg))
    (print-prompt))

  adapter/Lifecycle
  (init [this] this)
  (start [this]
    (adapter/emit robot :connected)
    (print-prompt)
    (loop []
      (let [line (read-line)]
        (print-prompt)
        (when-not (or (nil? line) (= line "exit"))
          (adapter/receive robot line)
          (recur))))
    (adapter/emit robot :close))
  (stop [this]))

#_(defn shell [robot]
  (->ShellAdapter robot))
