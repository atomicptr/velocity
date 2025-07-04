(ns app.controller.health
  (:require
   [app.utils.html :as html]))

(defn up [_req] (html/ok "OK"))
