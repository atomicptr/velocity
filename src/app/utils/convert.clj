(ns app.utils.convert)

(defn str->int [str]
  (assert (string? str))
  (Integer/new str))
