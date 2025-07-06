(ns app.views.layout
  (:require
   [app.config :refer [conf]]))

(defn root [m elems]
  [:html
   [:head
    [:title (if (get-in m [:title])
              (str (:title m) " | " (conf :app :name))
              (conf :app :name))]
    [:link {:href "/out.css" :rel "stylesheet"}]]
   [:body
    [:main elems]
    [:script {:src "https://cdn.jsdelivr.net/npm/htmx.org@2.0.6/dist/htmx.min.js"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/alpinejs@3.14.9/dist/cdn.min.js"}]]])

(defn base
  ([elems] (base {} elems))
  ([m elems] (root m elems)))

(defn app
  ([elems] (app {} elems))
  ([m elems] (root m elems)))

