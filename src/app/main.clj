(ns app.main
  (:require
   [app.config :refer [conf]]
   [app.database.core :as db]
   [app.routes :refer [routes]]
   [clojure.tools.logging :as log]
   [org.httpkit.server :as hks]
   [reitit.ring :as reitit-ring]
   [ring.middleware.cookies :refer [wrap-cookies]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.reload :refer [wrap-reload]])
  (:gen-class))

(def app
  (-> routes
      (reitit-ring/router)
      (reitit-ring/ring-handler)
      (wrap-cookies)
      (wrap-params {:encoding "UTF-8"})))

(defonce server (atom nil))

(defn stop! []
  (when @server
    (@server :timeout 100)))

(defn start! []
  (db/init! (conf :database :url))
  (log/info "starting server at port... " (conf :http :port) "in env" (conf :env))
  (reset! server
          (hks/run-server
           (if (= :dev (conf :env))
             (wrap-reload #'app)
             app)
           {:ip   (conf :http :ip)
            :port (conf :http :port)
            :join? false})))

(defn restart! []
  (stop!)
  (start!))

(defn -main [& _args]
  (start!))

