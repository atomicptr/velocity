(ns app.controller.login
  (:require
   [app.config :refer [conf]]
   [app.database.session :as session]
   [app.database.users :as users]
   [app.utils.html :as html]
   [app.utils.htmx :as htmx]
   [app.utils.password :as password]
   [app.views.login :as view]
   [ring.util.response :refer [redirect]]))

(defn login [req]
  (if (session/is-authenticated? req)
    (redirect "/")
    (html/ok (view/login))))

(defn submit-login [req]
  (assert (not (session/is-authenticated? req)))
  (let [email (get-in req [:form-params "email"])
        password (get-in req [:form-params "password"])]
    (if-let [user (session/authenticate email password)]
      (let [session (session/create req user)]
        (-> (htmx/redirect "/")
            (assoc :session session)))
      (html/ok (view/login-form {:email {:value email
                                         :error "Could not log in, check email and/or password and try again"}})))))

(defn register [req]
  (if (or (not (conf :app :register-enabled?))
          (session/is-authenticated? req))
    (redirect "/login")
    (html/ok (view/register))))

(defn submit-register [req]
  (assert (not (session/is-authenticated? req)))
  (let [email (get-in req [:form-params "email"])
        password (get-in req [:form-params "password"])
        confirm-password (get-in req [:form-params "confirm-password"])]
    (cond
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
              (-> (htmx/redirect "/")
                  (assoc :session session))))))

(defn logout [req]
  (html/ok "OK"))

