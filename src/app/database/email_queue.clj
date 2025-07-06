(ns app.database.email-queue
  (:require
   [app.database.core :refer [database]]
   [app.database.query.email-queue :as emailq]))

(defn send! [recipient subject body]
  (emailq/insert-email! @database {:recipient recipient
                                   :subject   subject
                                   :body      body}))

(defn fetch [limit]
  (emailq/fetch-emails @database {:limit limit}))

