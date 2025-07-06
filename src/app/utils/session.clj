(ns app.utils.session
  (:require
   [app.utils.http :as http]
   [ring.util.response :refer [redirect]]))

(defn create [req user]
  (when user
    (merge (:session req) {:user       {:id    (:id user)
                                        :email (:email user)}
                           :ip         (http/get-ip req)
                           :user-agent (http/user-agent req)})))

(defn is-authenticated? [req]
  (not (nil? (get-in req [:session :user :id]))))

(defn wrap-privileged
  "Only allow access to privileged users, redirect to /login otherwise"
  [fun]
  (fn [req]
    (if (is-authenticated? req)
      (fun req)
      (redirect "/login"))))

(defn wrap-unprivileged
  "Only allow access to unprivileged users, redirect to / otherwise"
  [fun]
  (fn [req]
    (if (not (is-authenticated? req))
      (fun req)
      (redirect "/"))))
