(ns app.auth.views.components
  (:require
   [app.core.view.layout :as layout]))

(defn login-card
  ([m elems] (login-card m elems nil))
  ([m elems elems-under]
   (layout/base
    m
    [:div.center.flex-col.gap-2.h-screen
     [:div.card.bg-base-200
      [:div.card-body elems]]
     elems-under])))

(defn error-text [text]
  (when text
    [:p.text-error.text-xs text]))

(defn email-field [data]
  [:label.floating-label
   [:span "E-Mail"]
   [:input.input.w-full {:name "email"
                         :type "email"
                         :class (when (:error data) "input-error")
                         :value (:value data)}]
   (error-text (:error data))])

(defn password-field [data]
  [:label.floating-label
   [:span (:title data)]
   [:input.input.w-full {:name (:name data)
                         :type "password"
                         :class (when (:error data) "input-error")
                         :value (:value data)}]
   (error-text (:error data))])

(defn submit []
  [:button.btn.btn-primary {:type "submit"} "Submit"])
