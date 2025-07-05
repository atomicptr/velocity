(ns app.controller.index
  (:require
   [app.utils.html :as html]
   [app.views.layout :as layout]
   [ring.util.response :as response]))

(defn index [req]
  (println (:cookies req))
  (println (:session req))

  (let [session (:session req)
        count   (:count session 0)
        session (assoc session :count (inc count))]
    (-> (html/ok [:h2 (str "You have accessed the page " count " times")])
        (assoc :session session))))
