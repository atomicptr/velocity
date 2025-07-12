(ns app.health.controller
  (:require
   [app.views.html :as html]))

(defn up [_req] (html/ok "OK"))
