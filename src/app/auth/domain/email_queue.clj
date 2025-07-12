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

(defonce ^:private email-scheduler (atom nil))

(defn run-scheduler! [batch-size interval]
  (log/info "running email scheduler with batch size:" batch-size "interval: " interval)
  (reset! email-scheduler (chan))
  (go-loop []
    (let [[_ ch] (alts! [(timeout interval) @email-scheduler])]
      (when-not (= ch @email-scheduler)
        (try
          (let [emails (fetch batch-size)]
            (doseq [email emails]
              (send-email! email)
              ; assuming send-email! didnt throw, remove it from queue
              (remove! (:id email))))
          (catch Exception e
            (log/error "email queue: " (.getMessage e))))
        (recur)))))

(defn stop-scheduler! []
  (when-let [ch @email-scheduler]
    (close! ch)
    (reset! email-scheduler nil)))
