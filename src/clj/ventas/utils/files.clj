(ns ventas.utils.files
  (:import
   [org.apache.commons.io FilenameUtils]))

(defn get-tmp-dir []
  (System/getProperty "java.io.tmpdir"))

(defn extension [s]
  (FilenameUtils/getExtension s))

(defn basename [s]
  (FilenameUtils/getBaseName s))
