(ns app.auth.views.register
  (:require
   [app.auth.views.components :refer [email-field login-card password-field
                                      submit]]))

(defn register-form [data]
  [:form {:hx-post "/register" :hx-swap "outerHTML"}
   [:div.flex.flex-col.gap-4.mt-4
    (email-field {:value (get-in data [:email :value])
                  :error (get-in data [:email :error])})
    (password-field {:title "Password"
                     :name "password"
                     :value (get-in data [:password :value])
                     :error (get-in data [:password :error])})
    (password-field {:title "Confirm password"
                     :name "confirm-password"
                     :value (get-in data [:confirm-password :value])
                     :error (get-in data [:confirm-password :error])})
    (submit)]])

(defn register []
  (login-card
   {:title "Register"}
   [:div.flex.flex-col.gap-2.min-w-sm
    [:h2.text-2xl "Register"]
    [:div "Create a new account"]
    (register-form {})]))

(defn verify-email [email]
  [:div "An email verification link has been sent to " [:strong email] ", please click on the link to verify your account"])
