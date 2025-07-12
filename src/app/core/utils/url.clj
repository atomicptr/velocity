(ns app.core.utils.url
  (:require
   [ring.util.request :as request])
  (:import
   [java.net MalformedURLException URL]))

(defn valid? [^String url]
  (try (URL/new url) true
       (catch MalformedURLException _ false)))

(defn absolute [req path]
  (if (valid? path)
    path
    (let [url (URL/new (request/request-url req))]
      (str (URL/new url path)))))
