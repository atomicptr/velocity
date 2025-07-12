(ns app.auth.views.settings
  (:require
   [app.core.view.components.form :as form]
   [app.core.view.layout :as layout]))

(defn profile-info-form [data]
  [:form {:hx-post "/settings/update-profile-info"}
   [:div.flex.flex-col.gap-4
    (form/text-field  {:title "Name"
                       :name  "name"
                       :value (get-in data [:name :value])
                       :error (get-in data [:name :error])})
    (form/email-field {:name "email"
                       :value (get-in data [:email :value])
                       :error (get-in data [:email :error])})
    (when (:message data)
      [:div (:message data)])
    [:div.flex.flex-row.justify-end
     (form/submit)]]])

(defn update-password-form [data]
  [:form {:hx-post "/settings/update-password"}
   [:div.flex.flex-col.gap-4
    (form/password-field {:title "Current password"
                          :name  "password"
                          :error (get-in data [:password :error])})
    (form/password-field {:title "New password"
                          :name  "new-password"
                          :error (get-in data [:new-password :error])})
    (form/password-field {:title "Confirm new password"
                          :name  "confirm-new-password"
                          :error (get-in data [:confirm-new-password :error])})
    (when (:message data)
      [:div (:message data)])
    [:div.flex.flex-row.justify-end
     (form/submit)]]])

(defn settings [user]
  (layout/app
   {:user user :title "Profile"}
   [:div.mx-2
    ; update profile information
    [:div.flex.flex-col.sm:flex-row.gap-2.my-4
     [:div.px-4 {:class "sm:w-1/2"}
      [:h2.text-xl "Profile Information"]
      [:p "Update your account's profile information and email address"]]
     [:div.card.bg-base-200 {:class "sm:w-1/2"}
      [:div.card-body (profile-info-form {:name  {:value (:name user)}
                                          :email {:value (:email user)}})]]]

    [:hr.border-b-1.border-base-200.opacity-30.my-8]

    ; update password
    [:div.flex.flex-col.sm:flex-row.gap-2.my-4
     [:div.px-4 {:class "sm:w-1/2"}
      [:h2.text-xl "Update Password"]
      [:p "Ensure your account is using a long random password to stay secure"]]
     [:div.card.bg-base-200 {:class "sm:w-1/2"}
      [:div.card-body
       (update-password-form {})]]]]))

