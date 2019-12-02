(ns app
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def ignoreWords
  (conj
   (->>
    (io/resource "ignoreWords.txt")
    (io/reader)
    (line-seq)
    (into #{})
   ) "")
)

(defn analyzeFile
  [fileName]
  (->>
   (io/resource fileName)
   (io/reader)
   (line-seq)
   (mapcat #(string/split % #"\s+"))
   (filter (fn [word] (not (contains? ignoreWords word))))
   (frequencies)
  )
)
