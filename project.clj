(defproject velocity "0.1.0"
  :description "FIXME: write description"
  :url "https://github.com/atomicptr/velocity"
  :license {:name "MIT" :url "https://opensource.org/license/mit"}
  :dependencies [[com.github.seancorfield/next.jdbc      "1.3.1048"]
                 [com.layerware/hugsql                   "0.5.3"]
                 [com.layerware/hugsql-adapter-next-jdbc "0.5.3"]
                 [de.mkammerer/argon2-jvm                "2.12"]
                 [hiccup/hiccup                          "2.0.0"]
                 [http-kit/http-kit                      "2.8.0"]
                 [metosin/reitit                         "0.9.1"]
                 [migratus/migratus                      "1.6.4"]
                 [org.clojure/clojure                    "1.12.1"]
                 [org.clojure/tools.logging              "1.3.0"]
                 [org.xerial/sqlite-jdbc                 "3.50.2.0"]
                 [ring/ring-core                         "1.14.2"]
                 [ring/ring-devel                        "1.14.2"]]
  :main ^:skip-aot app.main
  :omit-source true
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
