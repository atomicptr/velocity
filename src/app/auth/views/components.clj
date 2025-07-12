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
