(ns app.dashboard.view
  (:require
   [app.core.view.layout :as layout]))

(defn dashboard [user]
  (layout/app {:user user :title "Dashboard"}
              [:h2.text-2xl "Hello World"]))
