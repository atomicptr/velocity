(ns app.routes
  (:require
   [app.auth.controllers.login :as login]
   [app.auth.controllers.profile :as profile]
   [app.auth.controllers.register :as register]
   [app.auth.controllers.reset-password :as reset-pw]
   [app.auth.domain.sessions :refer [wrap-privileged wrap-unprivileged]]
   [app.dashboard.controller :as dashboard]
   [app.health.controller :as health]))

(def routes
  [["/"                      {:get (wrap-privileged dashboard/index)}]

   ; login
   ["/login"                 {:get (wrap-unprivileged login/login)
                              :post (wrap-unprivileged login/submit-login)}]
   ["/logout"                {:get (wrap-privileged login/logout)}]
   ; register
   ["/register"              {:get (wrap-unprivileged register/register)
                              :post (wrap-unprivileged register/submit-register)}]
   ["/activate/:token"       {:get register/activate}]
   ; pw reset
   ["/reset-password"        {:get (wrap-unprivileged reset-pw/reset-password)
                              :post (wrap-unprivileged reset-pw/submit-reset-password)}]
   ["/reset-password/:token" {:get (wrap-unprivileged reset-pw/reset-password-with-token)
                              :post (wrap-unprivileged reset-pw/submit-reset-password-with-token)}]
   ; profile
   ["/profile"               {:get (wrap-privileged profile/profile)}]

   ; misc
   ["/health/up"             {:get health/up}]])
