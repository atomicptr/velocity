(ns app.core.utils.convert)

(defn str->int [str]
  (assert (string? str))
  (Integer/new str))

(defn bytes->hex [bytes]
  (apply str (map #(format "%02x" (bit-and % 0xFF)) bytes)))

