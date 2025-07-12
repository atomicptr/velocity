(ns app.core.view.components.form)

(defn error-text [text]
  (when text
    [:p.text-error.text-xs text]))

(defn text-field [data]
  [:label.floating-label
   [:span (:title data)]
   [:input.input.w-full {:name (:name data)
                         :type (or (:type data) "text")
                         :class (when (:error data) "input-error")
                         :value (:value data)}]
   (error-text (:error data))])

(defn email-field [data]
  (text-field (merge {:title "E-Mail" :name "email" :type "email"} data)))

(defn password-field [data]
  (text-field (merge {:type "password"} data)))

(defn submit
  ([] (submit "Submit"))
  ([text] [:button.btn.btn-primary {:type "submit"} text]))
