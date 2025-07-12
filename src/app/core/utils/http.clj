(ns app.core.utils.http
  (:require
   [clojure.string :as string]))

(defn get-ip [req]
  (or (let [ips (get-in req [:headers "x-forwarded-for"])]
        (when ips
          (-> ips (string/split #",") first)))
      (:remote-addr req)))

(defn user-agent [req]
  (get-in req [:headers "user-agent"]))
