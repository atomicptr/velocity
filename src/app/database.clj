(ns app.database
  (:require
   [clojure.tools.logging :as log]
   [hugsql.adapter.next-jdbc :as next-adapter]
   [hugsql.core :as hugsql]
   [migratus.core :as migratus]
   [next.jdbc :as jdbc]
   [next.jdbc.connection :refer [jdbc-url]]))

(defonce database (atom nil))
(defonce spec (atom nil))

(defn make-url [m]
  (jdbc-url m))

(defn- make-migratus-config []
  (assert (not (nil? @spec)))
  {:store          :database
   :migrations-dir "migrations"
   :db             @spec})

(defn- run-migrations! []
  (log/info "Running Migrations")
  (let [result (migratus/migrate (make-migratus-config))]
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
  (hugsql/set-adapter! (next-adapter/hugsql-adapter-next-jdbc))
  (run-migrations!)
  (connect!))

