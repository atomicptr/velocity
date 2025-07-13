(ns app.core.responder.html
  (:require [hiccup2.core :as h]))

(defn to-html [elems]
  (-> elems
      h/html
      str))

(defn response [status body & {:as headers}]
  {:status status :body (to-html body) :headers (merge {"Content-Type" "text/html"} headers)})

(def ok (partial response 200))

(def not-found (partial response 404))

(def internal-server-error (partial response 500))
