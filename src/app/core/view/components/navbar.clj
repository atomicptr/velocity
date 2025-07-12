(ns app.core.view.components.navbar
  (:require
   [app.auth.domain.users :as users]
   [app.config :refer [conf]]))

(defn- create-items [item]
  [:li [:a {:href (:link item)} (:title item)]])

(defn navbar [items user]
  [:div.navbar.bg-base-200.shadow-sm.sticky.top-0.mb-12.px-4.z-50

   ; icon
   [:div.flex-none
    [:a.btn.btn-ghost.text-xl {:href "/"} (conf :app :name)]]

   ; center area
   [:div.flex-1.mx-2
    [:ul.menu.menu-horizontal.px-1
     (map create-items items)]]

   ; right area
   (when user
     [:div.flex-none
      [:div.dropdown.dropdown-end
       [:div.btn.btn-ghost.btn-circle.avatar
        {:tabindex 0
         :role "button"
         :title (or (:name user) (:email user))}
        [:div.w-10.rounded-full
         [:img
          {:alt (or (:name user) (:email user))
           :src (users/gravatar (:email user))}]]]
       [:ul.menu.menu-sm.dropdown-content.bg-base-300.rounded-box.mt-3.w-52.p-2.shadow
        {:tabindex 0}
        [:li
         [:a {:href "/settings"} "Settings"]]
        [:li [:a {:href "/logout"} "Logout"]]]]])])

