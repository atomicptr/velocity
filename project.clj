(defproject velocity "0.1.0"
  :description "FIXME: write description"
  :url "https://github.com/atomicptr/velocity"
  :license {:name "MIT" :url "https://opensource.org/license/mit"}
  :dependencies [[com.draines/postal                     "2.0.5"]
                 [com.github.seancorfield/next.jdbc      "1.3.1048"]
                 [com.taoensso/timbre                    "6.7.1"]
                 [de.mkammerer/argon2-jvm                "2.12"]
                 [hiccup/hiccup                          "2.0.0"]
                 [http-kit/http-kit                      "2.8.0"]
                 [metosin/reitit                         "0.9.1"]
                 [migratus/migratus                      "1.6.4"]
                 [org.clj-commons/digest                 "1.4.100"]
                 [org.clojure/clojure                    "1.12.1"]
                 [org.clojure/core.async                 "1.8.741"]
                 [org.xerial/sqlite-jdbc                 "3.50.2.0"]
                 [prone/prone                            "2021-04-23"]
                 [ring/ring-core                         "1.14.2"]
                 [ring/ring-devel                        "1.14.2"]]
  :plugins      [[lein-ancient/lein-ancient              "1.0.0-RC3"]]
  :main ^:skip-aot app.main
  :omit-source true
  :target-path "target/%s"
  :jvm-opts ["-Djava.util.logging.config.file=resources/logging.properties"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
