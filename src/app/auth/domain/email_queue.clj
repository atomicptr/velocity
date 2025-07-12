(ns app.auth.domain.email-queue
  (:require
   [app.auth.query.email-queue :as emailq]
   [app.database :refer [database]]))

(defn send! [recipient subject body]
  (emailq/insert-email! @database {:recipient recipient
                                   :subject   subject
                                   :body      body}))

(defn fetch [limit]
  (emailq/fetch-emails @database {:limit limit}))

