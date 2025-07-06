(ns app.database.session
  (:require
   [app.database.core :refer [database]]
   [app.database.query.sessions :as sessionq]
   [clojure.core :as core]
   [ring.middleware.session.store :refer [SessionStore]]))

(defn- make-session-id []
  (str (java.util.UUID/randomUUID)))

(deftype DatabaseStore []
  SessionStore

  (read-session [_ k]
    (when k
      (let [data (sessionq/get-session-data @database {:session-id k})
            data (:data data)]
        (when data
          (core/read-string data)))))

  (write-session [_ k v]
    (let [k (or k (make-session-id))
          user-id (get-in v [:user :id])]
      (sessionq/update-session-data
       @database
       {:session-id k
        :user-id user-id
        :data (core/prn-str v)})
      k))

  (delete-session [_ k]
    (when k
      (sessionq/delete-session @database {:session-id k})
      nil)))

(defn make-store [] (DatabaseStore/new))

