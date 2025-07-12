(ns app.auth.controllers.settings
  (:require
   [app.auth.domain.users :as users]
   [app.auth.utils.password :as password]
   [app.auth.views.settings :as view]
   [app.core.view.html :as html]))

(defn settings [req]
  (html/ok (view/settings (:user req))))

(defn update-profile-info [req] nil)

(defn update-password [req]

  (let [password             (get-in req [:form-params "password"])
        new-password         (get-in req [:form-params "new-password"])
        confirm-new-password (get-in req [:form-params "confirm-new-password"])]
    (cond
      (not (= new-password confirm-new-password))
      (html/ok (view/update-password-form {:confirm-new-password {:error "Password doesn't match"}}))

      (not (password/valid? new-password))
      (html/ok (view/update-password-form {:new-password {:error (password/reason password)}}))

      (not (password/verify-hash password (get-in req [:user :password])))
      (html/ok (view/update-password-form {:password {:error "Your password is not correct"}}))

      :else (do (users/update-password-via-id! (get-in req [:user :id]) new-password)
                (html/ok (view/update-password-form {:message "Your password has been successfully updated!"}))))))

