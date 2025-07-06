(ns app.utils.email)

(def valid? (partial re-matches #".+\@.+\..+"))

