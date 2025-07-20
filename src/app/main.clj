(ns app.main
  (:require
   [app.auth.database-store :as database-store]
   [app.config :refer [conf]]
   [app.database :as db]
   [app.routes :refer [routes]]
   [app.scheduler :refer [run-scheduler! stop-scheduler!]]
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
      (wrap-session {:store (database-store/make-store)
                     :cookie-attrs {:max-age   (conf :security :session :timeout)
                                    :same-site :strict
                                    :secure    true
                                    :http-only true}})
      (wrap-keyword-params)
      (wrap-params {:encoding "UTF-8"})
      (wrap-multipart-params)))

(defonce server (atom nil))

(defn stop! []
  (stop-scheduler!)
  (when @server
    (@server :timeout 100)))

(defn start! []
  (db/init! (conf :database :url))
  (run-scheduler! (conf :scheduler :tick-rate))
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

