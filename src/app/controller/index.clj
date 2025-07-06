(ns app.controller.index
  (:require
   [app.database.users :as users]
   [app.utils.html :as html]))

(defn index [req]
  (println (:cookies req))
  (println (:session req))

  (println (users/verified? (get-in req [:session :user :email])))

  (let [session (:session req)
        count   (:count session 0)
        session (assoc session :count (inc count))]
    (-> (html/ok [:h2 (str "You have accessed the page " count " times")])
        (assoc :session session))))
