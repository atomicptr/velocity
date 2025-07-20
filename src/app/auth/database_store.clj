(ns app.auth.database-store
  (:require
   [app.auth.domain.sessions :refer [delete-session! get-session-data
                                     upsert-session-data!]]
   [app.core.utils.uuid :as uuid]
   [clojure.core :as core]
   [ring.middleware.session.store :refer [SessionStore]]))

(deftype DatabaseStore []
  SessionStore

  (read-session [_ k]
    (when k
      (let [data (get-session-data k)
            data (:data data)]
        (when data
          (core/read-string data)))))

  (write-session [_ k v]
    (let [k          (or k (uuid/new))
          user-id    (get-in v [:user :id])
          ip         (get-in v [:ip])
          user-agent (get-in v [:user-agent])
          value      (dissoc v :ip :user-agent)]
      (upsert-session-data! k user-id ip user-agent (core/prn-str value))
      k))

  (delete-session [_ k]
    (when k
      (delete-session! k)
      nil)))

(defn make-store [] (DatabaseStore/new))
