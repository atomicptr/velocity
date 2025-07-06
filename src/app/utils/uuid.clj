(ns app.utils.uuid)

(defn new []
  (str (java.util.UUID/randomUUID)))
