(ns app.routes
  (:require
   [app.auth.controller :as login]
   [app.auth.domain.sessions :refer [wrap-privileged wrap-unprivileged]]
   [app.dashboard.controller :as dashboard]
   [app.health.controller :as health]))

(def routes
  [["/"                      {:get (wrap-privileged dashboard/index)}]

   ; login related
   ["/login"                 {:get (wrap-unprivileged login/login)
                              :post (wrap-unprivileged login/submit-login)}]
   ["/register"              {:get (wrap-unprivileged login/register)
                              :post (wrap-unprivileged login/submit-register)}]
   ["/activate/:token"       {:get login/activate}]
   ["/logout"                {:get (wrap-privileged login/logout)}]
   ["/reset-password"        {:get (wrap-unprivileged login/reset-password)
                              :post (wrap-unprivileged login/submit-reset-password)}]
   ["/reset-password/:token" {:get (wrap-unprivileged login/reset-password-with-token)
                              :post (wrap-unprivileged login/submit-reset-password-with-token)}]

   ; misc
   ["/health/up"             {:get health/up}]])
