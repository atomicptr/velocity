(ns app.utils.hash
  (:require
   [app.utils.convert :refer [bytes->hex]])
  (:import
   [java.security MessageDigest]))

(defn sha3 [string]
  (let [algorithm (MessageDigest/getInstance "SHA3-256")
        raw (.digest algorithm (.getBytes string))]
    (bytes->hex raw)))

