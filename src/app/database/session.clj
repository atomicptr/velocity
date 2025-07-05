(ns app.database.session
  (:require
   [app.database.core :refer [database]]
   [app.database.query.users :as user-query]
   [app.utils.http :as http]
   [app.utils.password :as password]
   [app.utils.time :as time]))

(defn authenticate [email password]
  (let [user (user-query/find-user-by-email @database {:email email})]
    (when (password/verify-hash password (:password user)) user)))

(defn- make-session-id []
  (str (java.util.UUID/randomUUID)))

(defn create! [req user]
  (let [t          (time/now)
        session-id (make-session-id)]
    (user-query/create-session @database {:session-id session-id
                                          :user-id (:id user)
                                          :ip (http/get-ip req)
                                          :user-agent (http/user-agent req)
                                          :last-activity t
                                          :created-at t})
    session-id))

(defn is-authenticated? [req]
  (if-let [session-id (get-in req [:session :session-id])]
    (not (nil? (user-query/find-session @database {:session-id session-id})))
    false))
