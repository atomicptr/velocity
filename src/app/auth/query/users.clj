(ns app.auth.query.users
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "queries/users.sql")
