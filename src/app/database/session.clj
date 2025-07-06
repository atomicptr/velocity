(ns app.database.session
  (:require
   [app.database.core :refer [database]]
   [app.database.query.sessions :as sessionq]
   [app.database.query.users :as userq]
   [app.utils.http :as http]
   [app.utils.password :as password]
   [app.utils.time :as time]
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
    (let [k (or k (make-session-id))]
      (sessionq/update-session-data
       @database
       {:session-id k
        :data (core/prn-str v)})
      k))

  (delete-session [_ k]
    (when k
      (sessionq/delete-session @database {:session-id k})
      nil)))

(defn make-store [] (DatabaseStore/new))

(defn create [req user]
  (when user
    (merge (:session req) {:user {:id    (:id user)
                                  :email (:email user)}})))

(defn is-authenticated? [req]
  (not (nil? (get-in req [:session :user :id]))))

(defn authenticate [email password]
  (let [user (userq/find-user-by-email @database {:email email})]
    (when (password/verify-hash password (:password user)) user)))

