(ns app.scheduler
  (:require
   [app.auth.domain.email-queue :as email-queue]
   [app.auth.domain.users :as users]
   [app.config :refer [conf]]
   [clojure.core.async :refer [close! alts! chan go-loop timeout]]
   [clojure.tools.logging :as log]))

(defonce ^:private jobs
  [email-queue/scheduler-tick!
   users/clean-email-change-requests-scheduler!
   users/clean-password-reset-tokens-scheduler!])

(defonce ^:private scheduler (atom nil))

(defn run-scheduler! [tickrate-secs]
  (log/info "running scheduler with tick rate:" (conf :scheduler :tick-rate))
  (reset! scheduler (chan))
  (go-loop []
    (let [tick-rate (* tickrate-secs 1000)
          [_ ch]    (alts! [(timeout tick-rate) @scheduler])]
      (when-not (= ch @scheduler)
        (doseq [job jobs]
          (try
            (job)
            (catch Exception e
              (log/error "scheduler error:" (.getMessage e)))))
        (recur)))))

(defn stop-scheduler! []
  (when-let [ch @scheduler]
    (close! ch)
    (reset! scheduler nil)))
