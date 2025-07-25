(ns app.database
  (:require
   [migratus.core :as migratus]
   [next.jdbc :as jdbc]
   [next.jdbc.connection :refer [jdbc-url]]
   [next.jdbc.result-set :as rs]
   [taoensso.timbre :as log]))

(defonce database (atom nil))
(defonce spec (atom nil))

(defn make-url [m]
  (jdbc-url m))

(defn- make-migratus-config []
  (assert (not (nil? @spec)))
  {:store                :database
   :migrations-dir       "migrations"
   :init-script          "init.sql"
   :init-in-transaction? false
   :db                   @spec})

(defn- run-migrations! []
  (log/info "Running Migrations")
  (let [config (make-migratus-config)
        _      (migratus/init config)
        result (migratus/migrate config)]
    (if (and (map? result)
             (contains? result :error))
      (throw (ex-info "Migration failed: " result))
      (log/info "Migrations finished:" result))))

(defn- connect! []
  (assert (not (nil? @spec)))
  (when (nil? @database)
    (reset! database (jdbc/get-datasource @spec))
    (log/info "Connected to database" @spec)))

(defn init! [url]
  (reset! spec {:jdbcUrl url})
  (run-migrations!)
  (connect!))

(defn exec! [query]
  (jdbc/execute! @database query {:builder-fn rs/as-unqualified-lower-maps})); TODO: switch to unqualified kebab maps
