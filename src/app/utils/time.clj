(ns app.utils.time)

(defn now []
  (quot (System/currentTimeMillis) 1000))

