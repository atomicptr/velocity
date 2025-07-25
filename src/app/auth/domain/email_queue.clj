(ns app.auth.domain.email-queue
  (:require
   [app.config :refer [conf]]
   [app.database :refer [exec!]]
   [postal.core :refer [send-message]]
   [taoensso.timbre :as log]))

(defn send! [recipient subject body]
  (exec! ["insert into email_queue (recipient, subject, body, created_at)
           values (?, ?, ?, CURRENT_TIMESTAMP)"
          recipient
          subject
          body]))

(defn fetch [limit]
  (exec! ["select * from email_queue order by created_at asc limit ?" limit]))

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
  (exec! ["delete from email_queue where id = ?" id]))

(defn scheduler-tick! []
  (let [emails (fetch (conf :email :queue :batch-size))]
    (doseq [email emails]
      (send-email! email)
      ; assuming send-email! didnt throw, remove it from queue
      (remove! (:id email)))))

