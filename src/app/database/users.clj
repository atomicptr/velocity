(ns app.database.users
  (:require
   [app.database.core :refer [database]]
   [app.database.email-queue :as email-queue]
   [app.database.query.users :as userq]
   [app.utils.password :as password]
   [app.utils.url :as url]
   [app.utils.uuid :as uuid]))

(defn exists? [email]
  (not (nil? (userq/find-user-by-email @database {:email email}))))

(defn create! [email password]
  (let [password (password/create-hash password)]
    (userq/insert-user! @database {:email email
                                   :password password})
    (userq/find-user-by-email @database {:email email})))

(defn authenticate [email password]
  (let [user (userq/find-user-by-email @database {:email email})]
    (when (password/verify-hash password (:password user)) user)))

(defn create-password-reset-request! [req email]
  (when (userq/find-user-by-email @database {:email email})
    (let [token (uuid/new)]
      (userq/upsert-reset-password-token! @database {:email email :token token})
      (email-queue/send! email "Reset password" (str "Hello, you wanted to reset your password right?" (url/absolute req (str "/reset-password/" token)))))))

(defn reset-token-valid? [token]
  (not (nil? (userq/find-password-reset-token @database {:token token}))))

(defn update-password! [token password]
  (userq/update-password-via-token! @database {:token token :password (password/create-hash password)})
  (userq/delete-reset-token! @database {:token token}))
