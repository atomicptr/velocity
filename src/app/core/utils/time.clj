(ns app.core.utils.time)

(defn now []
  (quot (System/currentTimeMillis) 1000))

