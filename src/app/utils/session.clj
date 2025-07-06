(ns app.utils.session
  (:require
   [ring.util.response :refer [redirect]]))

(defn create [req user]
  (when user
    (merge (:session req) {:user {:id    (:id user)
                                  :email (:email user)}})))

(defn is-authenticated? [req]
  (not (nil? (get-in req [:session :user :id]))))

(defn wrap-privileged [fun]
  (fn [req]
    (if (is-authenticated? req)
      (fun req)
      (redirect "/login"))))
