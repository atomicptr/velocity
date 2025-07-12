(ns app.auth.utils.password
  (:require
   [app.config :refer [conf]])
  (:import
   [de.mkammerer.argon2 Argon2Factory]))

(defn- has-min-chars [minlen password]
  (>= (count password) minlen))

(def ^:private has-uppercase-char (partial some #(Character/isUpperCase %)))

(def ^:private has-lowercase-char (partial some #(Character/isLowerCase %)))

(def ^:private has-numeric-char (partial some #(Character/isDigit %)))

(defn reason [password]
  (cond
    (not (has-min-chars 12 password)) "Password needs to be at least 12 characters long"
    (not (has-uppercase-char password)) "Password needs at least one uppercase character"
    (not (has-lowercase-char password)) "Password needs at least one lowercase character"
    (not (has-numeric-char password)) "Password needs at least one numeric character"
    :else nil))

(defn valid? [password]
  (nil? (reason password)))

(defn create-hash [password]
  (.hash (Argon2Factory/create) (conf :password-hash :iterations) (conf :password-hash :memory) 1 password))

(defn verify-hash [password hashed-password]
  (.verify (Argon2Factory/create) hashed-password password))
