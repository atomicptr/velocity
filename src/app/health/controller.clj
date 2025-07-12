(ns app.health.controller
  (:require
   [app.core.view.html :as html]))

(defn up [_req] (html/ok "OK"))
