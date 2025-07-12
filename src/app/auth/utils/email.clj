(ns app.auth.utils.email)

(def valid? (partial re-matches #".+\@.+\..+"))

