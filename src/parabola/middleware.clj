(ns parabola.middleware)

(defn forwarding-to [reactors adapter]
  (fn [msg]
    (when-let [res (reactors msg)]
      (assoc res :adapter adapter))))

(defn only-when [reactors pred]
  (fn [msg]
    (when (pred msg)
      (reactors msg))))
