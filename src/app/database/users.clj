(ns app.database.users
  (:require
   [app.database.core :refer [database]]
   [app.database.query.users :as user-query]
   [app.utils.password :as password]
   [app.utils.time :as time]))

(defn exists? [email]
  (not (nil? (user-query/find-user-by-email @database {:email email}))))

(defn create! [email password]
  (let [t        (time/now)
        password (password/create-hash password)]
    (user-query/insert-user @database {:email email
                                       :password password
                                       :created-at t
                                       :updated-at t})
    (user-query/find-user-by-email @database {:email email})))

