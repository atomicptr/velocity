(ns app.auth.views.profile
  (:require
   [app.core.view.layout :as layout]))

(defn profile [user]
  (layout/app {:user user :title "Profile"} [:h2 (:email user)]))

