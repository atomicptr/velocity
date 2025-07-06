(ns app.database.session
  (:require
   [app.database.core :refer [database]]
   [app.database.query.sessions :as sessionq]
   [app.utils.uuid :as uuid]
   [clojure.core :as core]
   [ring.middleware.session.store :refer [SessionStore]]))

(deftype DatabaseStore []
  SessionStore

  (read-session [_ k]
    (when k
      (let [data (sessionq/get-session-data @database {:session-id k})
            data (:data data)]
        (when data
          (core/read-string data)))))

  (write-session [_ k v]
    (let [k          (or k (uuid/new))
          user-id    (get-in v [:user :id])
          ip         (get-in v [:ip])
          user-agent (get-in v [:user-agent])
          value      (dissoc v :ip :user-agent)]
      (sessionq/upsert-session-data!
       @database
       {:session-id k
        :user-id user-id
        :ip ip
        :user-agent user-agent
        :data (core/prn-str value)})
      k))

  (delete-session [_ k]
    (when k
      (sessionq/delete-session! @database {:session-id k})
      nil)))

(defn make-store [] (DatabaseStore/new))

