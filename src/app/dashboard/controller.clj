(ns app.dashboard.controller
  (:require
   [app.core.responder.html :as html]
   [app.dashboard.view :as view]))

(defn index [req]
  (html/ok (view/dashboard (:user req))))
