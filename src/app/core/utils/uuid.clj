(ns app.core.utils.uuid)

(defn new []
  (str (java.util.UUID/randomUUID)))
