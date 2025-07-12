(ns app.auth.controllers.profile
  (:require
   [app.auth.domain.users :as users]
   [app.auth.views.profile :as view]
   [app.core.view.html :as html]))

(defn profile [req]
  (let [user-id (get-in req [:session :user :id])
        user    (dissoc (users/find-by-id user-id) :password)]
    (html/ok (view/profile user))))
