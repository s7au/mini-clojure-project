(ns app
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

; list of words that don't/shouldn't mean anything.
; Obviously might not be complete
(def ignoreWords
  (conj
   (->>
    (io/resource "ignoreWords.txt")
    (io/reader)
    (line-seq)
    (into #{})
   ) "")
)
; personal note: remember it reads top down so helper functions come first
(defn- associationKey
  [wordA wordB]
  (let [wordA2 (string/lower-case wordA)
        wordB2 (string/lower-case wordB)]
    (if (neg? (compare wordA2 wordB2))
      (str wordA2 "-" wordB2)
      (str wordB2 "-" wordA2)
    )
  )
)

; outputs all combinations of words in a list of wordList
; There is a library for all unique combinations so might look for that
; although unsure if "a-b" == "b-a" can be preserved
(defn- outputAssociation
  [sentence]
  (let [sentenceVector (into [] sentence)
        sentenceLength (count sentenceVector)]
    (->>
      (for [
            x (range 0 (- sentenceLength 1))
            y (range (+ x 1) sentenceLength)
           ]
        (associationKey (nth sentenceVector x) (nth sentenceVector y))
      )
    )
  )
)

; basically the point of this function is get word frequencies
; as well as frequencies that two words show up together in sentences
; feels like I threw memory and speed constraints out the door...
(defn analyzeFile
  [fileName]
  (let [
    lineList
    (->>
     (io/resource fileName)
     (io/reader)
     (line-seq)
    )
    wordList
    (->> lineList
     (mapcat #(string/split % #"\s+"))
     (filter (fn [word] (not (contains? ignoreWords word))))
    )
    sentenceList
    (->> lineList
     (mapcat #(string/split % #"\."))
    )]
   (->>
    (frequencies wordList)
    (sort-by val)
    (reverse)
    (take 10)
    (println)
   )
   (->>
     (for [sentence sentenceList]
       (->>
         (list sentence) ; I need to know what this needed to be placed in a list
         (mapcat #(string/split % #"\s+"))
         (filter (fn [word] (not (contains? ignoreWords word))))
         (outputAssociation)
       )
     )
     (apply concat)
     (frequencies)
     (sort-by val)
     (reverse)
     (take 20)
    )
  )
)
