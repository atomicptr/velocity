(ns app.routes
  (:require
   [app.auth.controllers.login :as login]
   [app.auth.controllers.register :as register]
   [app.auth.controllers.reset-password :as reset-pw]
   [app.auth.controllers.settings :as settings]
   [app.auth.domain.sessions :refer [wrap-privileged wrap-unprivileged]]
   [app.dashboard.controller :as dashboard]
   [app.health.controller :as health]))

(def routes
  [["/"                             {:get  (wrap-privileged dashboard/index)}]

   ; login
   ["/login"                        {:get  (wrap-unprivileged login/login)
                                     :post (wrap-unprivileged login/submit-login)}]
   ["/logout"                       {:get  (wrap-privileged   login/logout)}]
   ; register
   ["/register"                     {:get  (wrap-unprivileged register/register)
                                     :post (wrap-unprivileged register/submit-register)}]
   ["/activate/:token"              {:get  (wrap-unprivileged register/activate)}]
   ; pw reset
   ["/reset-password"               {:get  (wrap-unprivileged reset-pw/reset-password)
                                     :post (wrap-unprivileged reset-pw/submit-reset-password)}]
   ["/reset-password/:token"        {:get  (wrap-unprivileged reset-pw/reset-password-with-token)
                                     :post (wrap-unprivileged reset-pw/submit-reset-password-with-token)}]
   ; settings
   ["/settings"                     {:get  (wrap-privileged settings/settings)}]
   ["/settings/update-profile-info" {:post (wrap-privileged settings/update-profile-info)}]
   ["/settings/update-email/:token" {:get  (wrap-privileged settings/update-email)}]
   ["/settings/update-password"     {:post (wrap-privileged settings/update-password)}]
   ["/settings/purge-sessions"      {:post (wrap-privileged settings/purge-other-sessions)}]

   ; misc
   ["/health/up"             {:get health/up}]])
