(ns app.auth.controller
  (:require
   [app.auth.domain.email-queue :as email-queue]
   [app.auth.domain.sessions :as sessions]
   [app.auth.domain.users :as users]
   [app.auth.utils.email :as email]
   [app.auth.utils.password :as password]
   [app.auth.views :as view]
   [app.config :refer [conf]]
   [app.core.utils.hash :refer [sha3]]
   [app.core.utils.url :as url]
   [app.core.view.email :as vemail]
   [app.core.view.html :as html]
   [app.core.view.htmx :as htmx]
   [ring.util.response :refer [redirect]]))

(defn login [_]
  (html/ok (view/login)))

(defn submit-login [req]
  (let [email (get-in req [:form-params "email"])
        password (get-in req [:form-params "password"])]
    (if-let [user (users/authenticate email password)]
      (if (not (nil? (:email_verified_at user)))
        (let [session (sessions/create req user)]
          (-> (htmx/redirect "/")
              (assoc :session session)))
        ; TODO: if enough time has passed since the last attempt, send another verification email
        (html/ok (view/login-form {:email {:value email
                                           :error "This account has not been verified yet, please check your emails"}})))
      (html/ok (view/login-form {:email {:value email
                                         :error "Could not log in, check email and/or password and try again"}})))))

(defn register [_]
  (if (not (conf :app :register-enabled?))
    (redirect "/login")
    (html/ok (view/register))))

(defn- activation-token [email]
  (sha3 (str "activation-token" (conf :security :secret) email)))

(defn submit-register [req]
  (let [email            (get-in req [:form-params "email"])
        password         (get-in req [:form-params "password"])
        confirm-password (get-in req [:form-params "confirm-password"])]
    (cond
      (not (email/valid? email))
      (html/ok (view/register-form {:email {:value email
                                            :error "E-Mail is invalid"}}))
      (not (= password confirm-password))
      (html/ok (view/register-form {:email            {:value email}
                                    :confirm-password {:error "Password doesn't match"}}))
      (not (password/valid? password))
      (html/ok (view/register-form {:email    {:value email}
                                    :password {:error (password/reason password)}}))
      (users/exists? email)
      (html/ok (view/register-form {:email    {:value email
                                               :error "User with this E-Mail address already exists"}}))
      :else (do (users/create! email password)
                (email-queue/send!
                 email
                 "Account registration"
                 (vemail/activation (url/absolute req (str "/activate/" (activation-token email) "?email=" email))))
                (html/ok (view/verify-email email))))))

(defn activate [req]
  (let [token (get-in req [:path-params :token])
        email (get-in req [:query-params "email"])]
    (if (= token (activation-token email))
      (let [user (users/find-by-email email)]
        (if (nil? (:email_verified_at user))
          (do (users/verify-email! email)
              (let [session (sessions/create req user)]
                (-> (redirect "/")
                    (assoc :session session))))
          (redirect "/login")))

      (redirect "/login"))))

(defn logout [_req]
  (-> (redirect "/login")
      (assoc :session nil)))

(defn reset-password [_]
  (html/ok (view/reset-password)))

(defn submit-reset-password [req]
  (let [email (get-in req [:form-params "email"])]
    (if (email/valid? email)
      (do
        (users/create-password-reset-request! req email)
        (html/ok (view/reset-password-success email)))
      (html/ok (view/reset-password-form {:email {:value email
                                                  :error "E-Mail is invalid"}})))))

(defn reset-password-with-token [req]
  (let [token (get-in req [:path-params :token])]
    (if (users/reset-token-valid? token)
      (html/ok (view/reset-password-with-token token))
      (redirect "/login"))))

(defn submit-reset-password-with-token [req]
  (let [token            (get-in req [:path-params :token])
        password         (get-in req [:form-params "password"])
        confirm-password (get-in req [:form-params "confirm-password"])]
    (cond
      (not (= password confirm-password))
      (html/ok (view/register-form {:confirm-password {:error "Password doesn't match"}}))
      (not (password/valid? password))
      (html/ok (view/register-form {:password {:error (password/reason password)}}))
      :else (do (users/update-password! token password)
                (htmx/redirect "/login")))))

