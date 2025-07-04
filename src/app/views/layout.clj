(ns app.views.layout)

(defn root [elems]
  [:html [:head [:title "Hello, World!"]]
   [:body [:main elems]]])

(defn app [elems]
  (root elems))
