(ns app.auth.controllers.reset-password
  (:require
   [app.auth.domain.users :as users]
   [app.auth.utils.email :as email]
   [app.auth.utils.password :as password]
   [app.auth.views.register :as view-register]
   [app.auth.views.reset-password :as view]
   [app.core.responder.html :as html]
   [app.core.responder.htmx :as htmx]
   [ring.util.response :refer [redirect]]))

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
      (html/ok (view-register/register-form {:confirm-password {:error "Password doesn't match"}}))
      (not (password/valid? password))
      (html/ok (view-register/register-form {:password {:error (password/reason password)}}))
      :else (do (users/update-password-via-token! token password)
                (htmx/redirect "/login")))))

