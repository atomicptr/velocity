(ns app.auth.domain.sessions
  (:require
   [app.auth.domain.users :as users]
   [app.auth.query.sessions :as sessionq]
   [app.core.utils.http :as http]
   [app.core.utils.uuid :as uuid]
   [app.database :refer [database]]
   [clojure.core :as core]
   [ring.middleware.session.store :refer [SessionStore]]
   [ring.util.response :refer [redirect]]))

(deftype DatabaseStore []
  SessionStore

  (read-session [_ k]
    (when k
      (let [data (sessionq/get-session-data @database {:session-id k})
            data (:data data)]
        (when data
          (core/read-string data)))))

  (write-session [_ k v]
    (let [k          (or k (uuid/new))
          user-id    (get-in v [:user :id])
          ip         (get-in v [:ip])
          user-agent (get-in v [:user-agent])
          value      (dissoc v :ip :user-agent)]
      (sessionq/upsert-session-data!
       @database
       {:session-id k
        :user-id user-id
        :ip ip
        :user-agent user-agent
        :data (core/prn-str value)})
      k))

  (delete-session [_ k]
    (when k
      (sessionq/delete-session! @database {:session-id k})
      nil)))

(defn make-store [] (DatabaseStore/new))

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
  (sessionq/find-sessions @database {:user-id (:id user)}))

(defn purge-other-sessions! [user session-id]
  (sessionq/purge-other-sessions! @database {:user-id    (:id user)
                                             :session-id session-id}))
