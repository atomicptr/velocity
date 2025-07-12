(ns app.views.htmx)

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(defn redirect [to-url]
  (response 200 "" "HX-Redirect" to-url))
