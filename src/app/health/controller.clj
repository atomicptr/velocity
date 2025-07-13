(ns app.health.controller
  (:require
   [app.core.responder.html :as html]))

(defn up [_req] (html/ok "OK"))
