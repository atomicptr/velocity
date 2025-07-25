(ns app.config
  (:require
   [app.core.utils.collections :refer [deep-merge filter-nil-values]]
   [app.core.utils.env :as env]
   [app.database :as db]
   [clojure.string :refer [lower-case]]))

(def ^:private default-sender "velocity@atomicptr.dev")
(def ^:private default-secret "super-secret-key-that-you-should-change")
(def ^:private session-timeout 604800) ; 1 week

(defonce default-config
  {:env      :dev
   :app      {:name              "Velocity"
              :register-enabled? false}
   :http     {:ip                "0.0.0.0"
              :port              3000}
   :security {:secret            default-secret
              :session           {:timeout session-timeout}
              :password-hash     {:iterations 10
                                  :memory     65536}}
   :database {:url (db/make-url  {:dbtype     "sqlite"
                                  :dbname     "data/app.db"})}
   :email    {:sender            default-sender
              :smtp              {:host       "localhost"
                                  :user       nil
                                  :pass       nil
                                  :port       587
                                  :tls        true}
              :queue             {:batch-size 100}}
   :scheduler {:tick-rate        300}
   :logging                      {:min-level :info}
   :cleanup                      {:forgot-password-requests-older-than 3600
                                  :email-change-requests-older-than    3600}})

(defn- from-env! []
  (filter-nil-values
   {:env       (env/get-map! "APP_ENV" lower-case keyword)
    :app       {:name                 (env/get!        "APP_NAME")
                :register-enabled?    (env/get-bool!   "APP_REGISTER_ENABLED")}
    :http      {:ip                   (env/get!        "APP_IP")
                :port                 (env/get-int!    "APP_PORT")}
    :security  {:secret               (env/get!        "APP_SECRET")
                :session {:timeout    (env/get-int!    "APP_SESSION_TIMEOUT")}}
    :database  {:url                  (env/get!        "DATABASE_URL")}
    :email     {:sender               (env/get!        "APP_SENDER_EMAIL")
                :smtp    {:host       (env/get!        "SMTP_HOST")
                          :user       (env/get!        "SMTP_USER")
                          :pass       (env/get!        "SMTP_PASSWORD")
                          :port       (env/get-int!    "SMTP_PORT")
                          :tls        (env/get-bool!   "SMTP_USE_TLS")}
                :queue   {:batch-size (env/get-int!    "EMAIL_BATCH_SIZE")}}
    :logging             {:min-level  (env/get-keyword "APP_LOG_LEVEL")}
    :scheduler {:tick-rate            (env/get-int!    "SCHEDULER_TICK_RATE")}}))

(def config (deep-merge default-config (from-env!)))

(defn conf [& args] (get-in config args))
