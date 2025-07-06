(ns app.controller.login
  (:require
   [app.config :refer [conf]]
   [app.database.email-queue :as email-queue]
   [app.database.users :as users]
   [app.utils.email :as email]
   [app.utils.hash :refer [sha3]]
   [app.utils.html :as html]
   [app.utils.htmx :as htmx]
   [app.utils.password :as password]
   [app.utils.session :as session]
   [app.utils.url :as url]
   [app.views.email :as vemail]
   [app.views.login :as view]
   [fipp.clojure :refer [pprint]]
   [ring.util.response :refer [redirect]]))

(defn login [_]
  (html/ok (view/login)))

(defn submit-login [req]
  (let [email (get-in req [:form-params "email"])
        password (get-in req [:form-params "password"])]
    (if-let [user (users/authenticate email password)]
      (let [session (session/create req user)]
        (-> (htmx/redirect "/")
            (assoc :session session)))
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
      :else (let [user (users/create! email password)
                  session (session/create req user)]
              (email-queue/send!
               email
               "Account registration"
               (vemail/activation (url/absolute req (str "/activate/" (activation-token email) "?email=" email))))
              (-> (htmx/redirect "/")
                  (assoc :session session))))))

(defn activate [req]
  (let [token (get-in req [:path-params :token])
        email (get-in req [:query-params "email"])]
    (when (= token (activation-token email))
      (users/verify-email! email))
    (redirect "/login")))

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

