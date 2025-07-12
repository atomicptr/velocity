(ns app.auth.views.reset-password
  (:require
   [app.auth.views.components :refer [login-card]]
   [app.core.view.components.form :refer [email-field password-field submit]]))

(defn reset-password-form [data]
  [:form {:hx-post "/reset-password" :hx-swap "outerHTML"}
   [:div.flex.flex-col.gap-4.mt-4
    (email-field {:value (get-in data [:email :value])
                  :error (get-in data [:email :error])})
    (submit)]])

(defn reset-password []
  (login-card
   {:title "Forgot password"}
   [:div.flex.flex-col.gap-2.min-w-sm
    [:h2.text-2xl "Forgot password"]
    [:div {:hx-target "this"}
     [:div "Send a password reset link to your email"]
     (reset-password-form {})]]
   [:div.flex.flex-row.gap-2
    [:a.btn.btn-ghost {:href "/login"} "Back to login"]]))

(defn reset-password-success [email]
  [:div "Sent a password reset link to " [:strong email] ", if there was an account associated with it"])

(defn reset-password-with-token-form [token data]
  [:form {:hx-post (str "/reset-password/" token) :hx-swap "outerHTML"}
   [:div.flex.flex-col.gap-4.mt-4
    (password-field {:title "Password"
                     :name "password"
                     :value (get-in data [:password :value])
                     :error (get-in data [:password :error])})
    (password-field {:title "Confirm password"
                     :name "confirm-password"
                     :value (get-in data [:confirm-password :value])
                     :error (get-in data [:confirm-password :error])})
    (submit)]])

(defn reset-password-with-token [token]
  (login-card
   {:title "Reset password"}
   [:div.flex.flex-col.gap-2.min-w-sm
    [:h2.text-2xl "Reset password"]
    (reset-password-with-token-form token {})]
   [:div.flex.flex-row.gap-2
    [:a.btn.btn-ghost {:href "/login"} "Back to login"]]))
