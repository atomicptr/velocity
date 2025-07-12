(ns app.main
  (:require
   [app.auth.domain.email-queue :as email-queue]
   [app.auth.domain.sessions :as sessions]
   [app.config :refer [conf]]
   [app.database :as db]
   [app.routes :refer [routes]]
   [clojure.tools.logging :as log]
   [org.httpkit.server :as hks]
   [prone.middleware :as prone]
   [reitit.ring :as reitit-ring]
   [ring.middleware.cookies :refer [wrap-cookies]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.middleware.session :refer [wrap-session]])
  (:gen-class))

(def app
  (-> routes
      (reitit-ring/router)
      (reitit-ring/ring-handler)
      (wrap-resource "public")
      (wrap-cookies)
      (wrap-session {:store (sessions/make-store)
                     :cookie-attrs {:max-age   (conf :security :session :timeout)
                                    :same-site :strict
                                    :secure    true
                                    :http-only true}})
      (wrap-keyword-params)
      (wrap-params {:encoding "UTF-8"})
      (wrap-multipart-params)))

(defonce server (atom nil))

(defn stop! []
  (email-queue/stop-scheduler!)
  (when @server
    (@server :timeout 100)))

(defn start! []
  (db/init! (conf :database :url))
  (email-queue/run-scheduler! (conf :email :queue :batch-size)
                              (* (conf :email :queue :interval) 1000))
  (log/info "starting server at port... " (conf :http :port) "in env" (conf :env))
  (reset! server
          (hks/run-server
           (if (= :dev (conf :env))
             (prone/wrap-exceptions (wrap-reload #'app))
             app)
           {:ip   (conf :http :ip)
            :port (conf :http :port)
            :join? false})))

(defn restart! []
  (stop!)
  (start!))

(defn -main [& _args]
  (start!))

