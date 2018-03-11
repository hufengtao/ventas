(ns ventas.test-tools
  (:require
   [datomic.api :as d]
   [ventas.database :as db]
   [ventas.database.schema :as schema]
   [taoensso.timbre :as timbre]))

(defn create-test-uri [& [id]]
  (str "datomic:mem://" (or id (gensym "test"))))

(defn test-conn [& [id]]
  (let [uri (create-test-uri id)]
    (d/create-database uri)
    (let [c (d/connect uri)]
      (with-redefs [db/db c]
        (timbre/with-level :report
          (schema/migrate)))
      c)))