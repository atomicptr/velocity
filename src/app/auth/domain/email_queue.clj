(ns app.auth.domain.email-queue
  (:require
   [app.auth.query.email-queue :as emailq]
   [app.config :refer [conf]]
   [app.database :refer [database]]
   [clojure.core.async :refer [alts! chan close! go-loop timeout]]
   [clojure.tools.logging :as log]
   [postal.core :refer [send-message]]))

(defn send! [recipient subject body]
  (emailq/insert-email! @database {:recipient recipient
                                   :subject   subject
                                   :body      body}))

(defn fetch [limit]
  (emailq/fetch-emails @database {:limit limit}))

(defn- send-email! [email]
  (let [res (send-message
             (conf :email :smtp)
             {:from    (conf :email :sender)
              :to      (:recipient email)
              :subject (:subject email)
              :body    (:body email)})]
    (when (not= (:error res) :SUCCESS)
      (log/error "could not sent message"
                 {:recipient (:recipient email)
                  :subject   (:subject   email)}
                 res))))

(defn remove! [id]
  (emailq/delete! @database {:id id}))

(defn scheduler-tick! []
  (let [emails (fetch (conf :email :queue :batch-size))]
    (doseq [email emails]
      (send-email! email)
      ; assuming send-email! didnt throw, remove it from queue
      (remove! (:id email)))))

