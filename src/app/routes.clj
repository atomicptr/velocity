(ns app.routes
  (:require
   [app.controller.health :as health]
   [app.controller.index :as index]
   [app.controller.login :as login]
   [app.utils.session :refer [wrap-privileged wrap-unprivileged]]))

(def routes
  [["/" {:get (wrap-privileged index/index)}]

   ; login related
   ["/login"          {:get (wrap-unprivileged login/login)
                       :post (wrap-unprivileged login/submit-login)}]
   ["/register"       {:get (wrap-unprivileged login/register)
                       :post (wrap-unprivileged login/submit-register)}]
   ["/logout"         {:get (wrap-privileged login/logout)}]
   ["/reset-password" {:get (wrap-unprivileged login/reset-password)
                       :post (wrap-unprivileged login/submit-reset-password)}]

   ; misc
   ["/health/up" {:get health/up}]])
