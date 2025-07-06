(ns app.database.query.sessions
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "queries/sessions.sql")
