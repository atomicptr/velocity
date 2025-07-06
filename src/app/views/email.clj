(ns app.views.email)

(defn activation [url]
  (str "Please confirm your email by clicking on this link: " url))

(defn reset-password [url]
  (str "To reset your password please use the following link: " url))
