(ns app.auth.views.email)

(defn activation [url]
  (str "Please confirm your email by clicking on this link: " url))

(defn reset-password [url]
  (str "To reset your password please use the following link: " url))

(defn email-change-request [url]
  (str "To change your email address click the following link: " url))
