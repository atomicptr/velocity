(ns app.auth.domain.users
  (:require
   [app.auth.domain.email-queue :as email-queue]
   [app.auth.query.users :as userq]
   [app.auth.utils.password :as password]
   [app.database :refer [database]]
   [app.utils.url :as url]
   [app.utils.uuid :as uuid]
   [app.views.email :as vemail]))

(defn find-by-email [email]
  (userq/find-user-by-email @database {:email email}))

(defn exists? [email]
  (not (nil? (userq/find-user-by-email @database {:email email}))))

(defn verified? [email]
  (let [user (userq/find-user-by-email @database {:email email})]
    (not (nil? (:email_verified_at user)))))

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
      (email-queue/send!
       email
       "Reset password"
       (vemail/reset-password (url/absolute req (str "/reset-password/" token)))))))

(defn reset-token-valid? [token]
  (not (nil? (userq/find-password-reset-token @database {:token token}))))

(defn update-password! [token password]
  (userq/update-password-via-token! @database {:token token :password (password/create-hash password)})
  (userq/delete-reset-token! @database {:token token}))

(defn verify-email! [email]
  (userq/verify-email! @database {:email email}))

