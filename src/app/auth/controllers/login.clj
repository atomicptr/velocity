(ns app.auth.controllers.login
  (:require
   [app.auth.domain.sessions :as sessions]
   [app.auth.domain.users :as users]
   [app.auth.views.login :as view]
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

(defn logout [_req]
  (-> (redirect "/login")
      (assoc :session nil)))

