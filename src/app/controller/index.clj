(ns app.controller.index
  (:require
   [app.utils.html :as html]
   [app.views.layout :as layout]))

(defn index [req]
  (html/ok (layout/app [:h1 "Hello, World"])))
