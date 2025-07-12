(ns app.config
  (:require
   [app.database :as db]
   [app.utils.collections :refer [deep-merge filter-nil-values]]
   [app.utils.env :as env]
   [clojure.string :refer [lower-case]]))

(def ^:private default-secret "super-secret-key-that-you-should-change")
(def ^:private session-timeout 604800) ; 1 week

(defonce default-config
  {:env      :dev
   :app      {:name "Velocity"
              :register-enabled? false}
   :http     {:ip   "0.0.0.0"
              :port 3000}
   :security {:secret default-secret
              :session {:timeout session-timeout}}
   :database {:url (db/make-url {:dbtype "sqlite" :dbname "data/app.db"})}})

(defn- from-env! []
  (filter-nil-values
   {:env      (env/get-map! "APP_ENV" lower-case keyword)
    :app      {:name (env/get! "APP_NAME")
               :register-enabled? (env/get-bool! "APP_REGISTER_ENABLED")}
    :http     {:ip   (env/get!     "APP_IP")
               :port (env/get-int! "APP_PORT")}
    :security {:secret (env/get!   "APP_SECRET")
               :session {:timeout (env/get-int! "APP_SESSION_TIMEOUT")}}
    :database {:url (env/get! "DATABASE_URL")}}))

(def config (deep-merge default-config (from-env!)))

(defn conf [& args] (get-in config args))
