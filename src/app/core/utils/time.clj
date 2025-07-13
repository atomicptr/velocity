(ns app.core.utils.time
  (:import
   [java.util TimeZone]))

(defn ago [datetime-str]
  (let [fmt (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss")
        _ (.setTimeZone fmt (TimeZone/getTimeZone "UTC"))
        date (.parse fmt datetime-str)
        now (java.util.Date.)
        diff (- (.getTime now) (.getTime date))
        seconds (quot diff 1000)
        minutes (quot seconds 60)
        hours (quot minutes 60)
        days (quot hours 24)]
    (cond
      (< seconds 60) (str seconds " seconds ago")
      (< minutes 60) (str minutes " minutes ago")
      (< hours 24) (str hours " hours ago")
      :else (str days " days ago"))))
