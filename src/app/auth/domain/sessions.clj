(ns app.auth.domain.sessions
  (:require
   [app.auth.domain.users :as users]
   [app.config :refer [conf]]
   [app.core.utils.http :as http]
   [app.core.utils.uuid :as uuid]
   [app.database :refer [exec!]]
   [clojure.core :as core]
   [ring.middleware.session.store :refer [SessionStore]]
   [ring.util.response :refer [redirect]]))

(defn get-session-data [session-id]
  (first (exec! ["select data from sessions where session_id = ?" session-id])))

(defn upsert-session-data! [session-id user-id ip user-agent data]
  (exec! ["insert into sessions (session_id, user_id, data, ip_address, user_agent, created_at, updated_at)
          values (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
          on conflict(session_id) do
          update set
              user_id=?,
              data=?,
              ip_address=COALESCE(?, sessions.ip_address),
              user_agent=COALESCE(?, sessions.user_agent),
              updated_at=CURRENT_TIMESTAMP"
          session-id
          user-id
          data
          ip
          user-agent
          user-id
          data
          ip
          user-agent]))

(defn delete-session! [session-id]
  (exec! ["delete from sessions where session_id = ?" session-id]))

(defn create [req user]
  (when user
    (merge (:session req) {:user       {:id    (:id user)
                                        :email (:email user)}
                           :ip         (http/get-ip req)
                           :user-agent (http/user-agent req)})))

(defn is-authenticated? [req]
  (not (nil? (get-in req [:session :user :id]))))

(defn wrap-privileged
  "Only allow access to privileged users, redirect to /login otherwise"
  [fun]
  (fn [req]
    (if (is-authenticated? req)
      ; TODO: if cant be found, remove session
      (fun (assoc req :user (users/find-by-id (get-in req [:session :user :id]))))
      (redirect "/login"))))

(defn wrap-unprivileged
  "Only allow access to unprivileged users, redirect to / otherwise"
  [fun]
  (fn [req]
    (if (not (is-authenticated? req))
      (fun req)
      (redirect "/"))))

(defn find-sessions [user]
  (exec! ["select * from sessions where user_id = ?" (:id user)]))

(defn purge-other-sessions! [user session-id]
  (exec! ["delete from sessions where user_id = ? and session_id != ?" (:id user) session-id]))

(defn clean-old-sessions-scheduler! []
  (let [threshold (conf :security :session :timeout)
        threshold-str (str "-" threshold " seconds")]
    (exec! ["delete from sessions where created_at <= DATETIME(CURRENT_TIMESTAMP, ?)" threshold-str])))
