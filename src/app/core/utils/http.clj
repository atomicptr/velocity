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

(defn parse-user-agent
  "Attempts to parse os and browser name from user agent"
  [ua-string]
  (let [ua (string/lower-case ua-string)
        browser-patterns
        {:opera #"opera\/[\d.]+|opr\/[\d.]+"
         :edge #"edge\/[\d.]+|edg\/[\d.]+"
         :chrome #"chrome\/[\d.]+"
         :firefox #"firefox\/[\d.]+"
         :safari #"safari\/[\d.]+"
         :ie #"msie [\d.]+|trident\/[\d.]+"}
        os-patterns
        {:windows #"windows nt [\d.]+"
         :macos #"mac os x [\d._]+"
         :android #"android [\d.]+"
         :ios #"iphone os [\d._]+|ipad; cpu os [\d._]+"
         :linux #"linux"}
        find-match (fn [patterns]
                     (some (fn [[k v]]
                             (when (re-find v ua) (name k)))
                           patterns))]
    {:browser (string/capitalize (or (find-match browser-patterns) "unknown"))
     :os (string/capitalize (or (find-match os-patterns) "unknown"))}))

