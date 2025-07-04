(ns app.config
  (:require
   [app.utils.collections :refer [deep-merge filter-nil-values]]
   [app.utils.env :as env]
   [app.database.core :as db]
   [clojure.string :refer [lower-case]]))

(defonce default-config
  {:env         :dev
   :http        {:ip   "0.0.0.0"
                 :port 3000}
   :database    {:url (db/make-url {:dbtype "h2" :dbname "data/app"})}})

(defn- from-env! []
  (filter-nil-values
   {:env  (env/get-map! "APP_ENV" lower-case keyword)
    :http {:ip   (env/get!     "APP_IP")
           :port (env/get-int! "APP_PORT")}}))

(def config (deep-merge default-config (from-env!)))

(defn conf [& args] (get-in config args))
