(ns app.auth.domain.users
  (:require
   [app.auth.domain.email-queue :as email-queue]
   [app.auth.utils.password :as password]
   [app.auth.views.email :as vemail]
   [app.config :refer [conf]]
   [app.core.utils.url :as url]
   [app.core.utils.uuid :as uuid]
   [app.database :refer [exec!]]
   [clj-commons.digest :as digest]))

(defn find-by-id [id]
  (first (exec! ["select * from users where id = ?" id])))

(defn find-by-email [email]
  (first (exec! ["select * from users where email = ?" email])))

(defn exists? [email]
  (not (nil? (find-by-email email))))

(defn create! [email password]
  (let [password (password/create-hash password)]
    (exec! ["insert into users (email, password, created_at, updated_at)
             values (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            email password])
    (find-by-email email)))

(defn authenticate [email password]
  (when-let [user (find-by-email email)]
    (when (password/verify-hash password (:password user)) user)))

(defn create-password-reset-request! [req email]
  (when (find-by-email email)
    (let [token (uuid/new)]
      (exec! ["insert into password_reset_tokens (email, token, created_at)
               values (?, ?, CURRENT_TIMESTAMP)
               on conflict(email) do update set token=:token, created_at = CURRENT_TIMESTAMP"
              email token token])
      (email-queue/send!
       email
       "Reset password"
       (vemail/reset-password (url/absolute req (str "/reset-password/" token)))))))

(defn reset-token-valid? [token]
  (not (nil? (exec! ["select * from password_reset_tokens where token = ?" token]))))

(defn update-password-via-id! [id password]
  (exec! ["update users
           set password = ?, updated_at = CURRENT_TIMESTAMP
           where id = ?"
          (password/create-hash password)
          id]))

(defn update-password-via-token! [token password]
  (exec! ["update users set password = ?, updated_at = CURRENT_TIMESTAMP
           where email in (select email from password_reset_tokens where token = ?)"
          (password/create-hash password)
          token])
  (exec! ["delete from password_reset_tokens where token = ?" token]))

(defn verify-email! [email]
  (exec! ["update users set email_verified_at = CURRENT_TIMESTAMP
          where email = ? and email_verified_at is null"
          email]))

(defn gravatar [email]
  (str "https://gravatar.com/avatar/" (digest/md5 email)))

(defn update-name! [user name]
  (assert (seq name))
  (exec! ["update users set name = ?, updated_at = CURRENT_TIMESTAMP where id = ?" name (:id user)]))

(defn update-email! [user new-email]
  (assert (seq new-email))
  (exec! ["update users set email = ?, updated_at = CURRENT_TIMESTAMP where id = ?" new-email (:id user)]))

(defn create-email-change-request! [req new-email]
  (let [user  (:user req)
        token (uuid/new)]
    (assert (not (nil? user)))
    (exec! ["insert into email_change_request (user_id, email, token, created_at)
             values (?, ?, ?, CURRENT_TIMESTAMP)
             on conflict(user_id) do update set email = ?, token = ?, created_at = CURRENT_TIMESTAMP"
            (:id user)
            new-email
            token
            new-email
            token])
    (email-queue/send!
     new-email
     "E-Mail Change Request"
     (vemail/email-change-request (url/absolute req (str "/settings/update-email/" token))))))

(defn find-email-change-request [user token]
  (first (exec! ["select * from email_change_request where user_id = ? and token = ?" (:id user) token])))

(defn remove-email-change-request! [user]
  (exec! ["delete from email_change_request where user_id = ?" (:id user)]))

(defn clean-password-reset-tokens-scheduler! []
  (let [threshold (conf :cleanup :forgot-password-requests-older-than)
        threshold-str (str "-" threshold " seconds")]
    (exec! ["delete from password_reset_tokens where created_at <= DATETIME(CURRENT_TIMESTAMP, ?)" threshold-str])))

(defn clean-email-change-requests-scheduler! []
  (let [threshold (conf :cleanup :forgot-password-requests-older-than)
        threshold-str (str "-" threshold " seconds")]
    (exec! ["delete from email_change_request where created_at <= DATETIME(CURRENT_TIMESTAMP, ?)" threshold-str])))
