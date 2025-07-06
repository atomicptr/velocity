(ns app.database.users
  (:require
   [app.database.core :refer [database]]
   [app.database.query.users :as userq]
   [app.utils.password :as password]))

(defn exists? [email]
  (not (nil? (userq/find-user-by-email @database {:email email}))))

(defn create! [email password]
  (let [password (password/create-hash password)]
    (userq/insert-user @database {:email email
                                  :password password})
    (userq/find-user-by-email @database {:email email})))

