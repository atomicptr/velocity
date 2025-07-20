(ns app.auth.controllers.settings
  (:require
   [app.auth.domain.sessions :as sessions]
   [app.auth.domain.users :as users]
   [app.auth.utils.email :as email]
   [app.auth.utils.password :as password]
   [app.auth.views.settings :as view]
   [app.core.responder.html :as html]
   [ring.util.response :refer [redirect]]))

(defn settings [req]
  (let [user (:user req)
        session-id (get-in req [:cookies "ring-session" :value])]
    (html/ok (view/settings user session-id (sessions/find-sessions user)))))

(defn update-profile-info [req]
  (let [user  (:user req)
        name  (get-in req [:form-params "name"])
        email (get-in req [:form-params "email"])]
    (cond
      (not (email/valid? email))
      (html/ok (view/profile-info-form {:name {:value name}
                                        :email {:value email
                                                :error "E-Mail is invalid"}}))

      :else
      (do
        (when (not= name (:name user))
          (users/update-name! user name))
        (if (not= email (:email user))
          (do (users/create-email-change-request! req email)
              (html/ok (view/profile-info-form {:name  {:value name}
                                                :email {:value email}
                                                :message "A confirmation email has been sent to your new email address!"})))
          (html/ok (view/profile-info-form {:name  {:value name}
                                            :email {:value email}
                                            :message "Name updated successfully"})))))))

(defn update-email [req]
  (let [user  (:user req)
        token (get-in req [:path-params :token])
        _     (assert (not (nil? token)))
        chreq (users/find-email-change-request user token)]
    (when chreq
      (users/update-email! user (:email chreq))
      (users/remove-email-change-request! user))
    (redirect "/")))

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

(defn purge-other-sessions [req]
  (let [user (:user req)
        session-id (get-in req [:cookies "ring-session" :value])]
    (sessions/purge-other-sessions! user session-id)
    (html/ok (view/session-manager {:session-id session-id
                                    :sessions (sessions/find-sessions user)}))))
