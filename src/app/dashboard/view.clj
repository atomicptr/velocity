(ns app.dashboard.view
  (:require
   [app.core.view.layout :as layout]))

(defn dashboard [user]
  (layout/app {:user user :title "Dashboard"}
              [:div.card.bg-base-200
               [:div.card-body
                [:h2.text-2xl "Welcome to Velocity"]
                [:div "Velocity is an application starter kit for Clojure that provides the perfect starting point for your next project."]
                [:h3.text-xl.my-2 "Powered by"]
                [:div.flex.flex-row.gap-2
                 [:a {:href "https://clojure.org" :target "_blank" :title "Clojure"}
                  [:img.h-16 {:src "https://clojure.org/images/clojure-logo-120b.png"}]]

                 [:a {:href "https://htmx.org" :target "_blank" :title "HTMX"}
                  [:div.h-16.flex.center.text-3xl.select-none
                   [:strong "<"] [:strong.text-info "/"] [:strong ">"]]]

                 [:a {:href "https://daisyui.com" :target "_blank" :title "Daisy UI"}
                  [:img.h-16 {:src "https://img.daisyui.com/images/daisyui/mark-static.svg"}]]]]]))
