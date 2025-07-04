(ns app.routes
  (:require
   [app.controller.health :as health]
   [app.controller.index :as index]))

(def routes
  [["/" {:get index/index}]
   ["/health/up" {:get health/up}]])
