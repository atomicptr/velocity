(ns app.core.utils.env
  (:require
   [app.core.utils.convert :refer [str->int]]
   [clojure.string :refer [lower-case]]))

(defn get-map! [name fun & funcs]
  (reduce (fn [acc f] (some-> acc f))
          (some-> (System/getenv name) fun)
          funcs))

(defn get! [name]
  (System/getenv name))

(defn get-int! [name]
  (get-map! name str->int))

(defn get-bool! [name]
  (case (get-map! name lower-case)
    "true" true
    "1"    true
    "on"   true
    false))
