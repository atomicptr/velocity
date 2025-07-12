(ns app.auth.views
  (:require
   [app.config :refer [conf]]
   [app.core.view.layout :as layout]))

(defn- login-card
  ([m elems] (login-card m elems nil))
  ([m elems elems-under]
   (layout/base
    m
    [:div.center.flex-col.gap-2.h-screen
     [:div.card.bg-base-200
      [:div.card-body elems]]
     elems-under])))

(defn- error-text [text]
  (when text
    [:p.text-error.text-xs text]))

(defn- email-field [data]
  [:label.floating-label
   [:span "E-Mail"]
   [:input.input.w-full {:name "email"
                         :type "email"
                         :class (when (:error data) "input-error")
                         :value (:value data)}]
   (error-text (:error data))])

(defn- password-field [data]
  [:label.floating-label
   [:span (:title data)]
   [:input.input.w-full {:name (:name data)
                         :type "password"
                         :class (when (:error data) "input-error")
                         :value (:value data)}]
   (error-text (:error data))])

(defn- submit []
  [:button.btn.btn-primary {:type "submit"} "Submit"])

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

(defn verify-email [email]
  [:div "An email verification link has been sent to " [:strong email] ", please click on the link to verify your account"])
