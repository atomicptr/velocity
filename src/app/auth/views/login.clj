(ns app.auth.views.login
  (:require
   [app.auth.views.components :refer [email-field login-card password-field
                                      submit]]
   [app.config :refer [conf]]))

(defn login-form [data]
  [:form {:hx-post "/login" :hx-swap "outerHTML"}
   [:div.flex.flex-col.gap-4.mt-4
    (email-field {:value (get-in data [:email :value])
                  :error (get-in data [:email :error])})
    (password-field {:title "Password"
                     :name "password"
                     :value (get-in data [:password :value])
                     :error (get-in data [:password :error])})
    (submit)]])

(defn login []
  (login-card
   {:title "Login"}
   [:div.flex.flex-col.gap-2.min-w-sm
    [:h2.text-2xl "Login"]
    [:div "Please log into your account."]
    (login-form {})]
   [:div.flex.flex-row.gap-2
    (when (conf :app :register-enabled?) [:a.btn.btn-ghost {:href "/register"} "Register"])
    [:a.btn.btn-ghost {:href "/reset-password"} "Forgot password"]]))
