(ns app.utils.collections)

(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

(defn filter-nil-values [m]
  (cond
    (map? m) (into {} (for [[k v] m :when (not (nil? v))]
                        [k (filter-nil-values v)]))
    (vector? m) (mapv filter-nil-values (remove nil? m))
    :else m))

