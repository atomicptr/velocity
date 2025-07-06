(ns app.routes
  (:require
   [app.controller.health :as health]
   [app.controller.index :as index]
   [app.controller.login :as login]
   [app.utils.session :as session]))

(def routes
  [["/" {:get (session/wrap-privileged index/index)}]

   ; login related
   ["/login"    {:get login/login
                 :post login/submit-login}]
   ["/register" {:get login/register
                 :post login/submit-register}]
   ["/logout"   {:get (session/wrap-privileged login/logout)}]

   ; misc
   ["/health/up" {:get health/up}]])
