(ns app.auth.domain.sessions
  (:require
   [app.auth.query.sessions :as sessionq]
   [app.database :refer [database]]
   [app.utils.http :as http]
   [app.utils.uuid :as uuid]
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
      (fun req)
      (redirect "/login"))))

(defn wrap-unprivileged
  "Only allow access to unprivileged users, redirect to / otherwise"
  [fun]
  (fn [req]
    (if (not (is-authenticated? req))
      (fun req)
      (redirect "/"))))
