(ns app.auth.query.email-queue
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "queries/email-queue.sql")
