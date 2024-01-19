(ns ica2sc.hypothesistest
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]) )


(defn read-csv [filename]
  (with-open [reader (io/reader filename)]
    (doall
      (csv/read-csv reader))))

(defn get-column [data column-name]
  (map #(get % column-name) data))

(defn count-occurrences [items]
  (frequencies items))

(defn most-common [occurrences]
  (first (sort-by val > occurrences)))

(defn calculate-age [birth-year]
  (- (Integer/parseInt (str (java.time.Year/now))) (Integer/parseInt birth-year)))

(defn determine-age-group [age]
  (cond
    (< age 18) "youth"
    (and (>= age 18) (< age 35)) "young-adult"
    (and (>= age 35) (< age 60)) "adult"
    :else "senior"))

(defn analyze-data [filename]
  (let [data (read-csv filename)
        headers (first data)
        rows (map (fn [row] (zipmap headers row)) (rest data))
        departures (get-column rows "DEPARTURE")
        destinations (get-column rows "DESTINATION")
        departure-counts (count-occurrences departures)
        destination-counts (count-occurrences destinations)
        ages (map calculate-age (get-column rows "YOB"))
        age-groups (map determine-age-group ages)
        age-group-counts (count-occurrences age-groups)
        adult-rows (filter (fn [row] (= (determine-age-group (calculate-age (get row "YOB"))) "adult")) rows)
        adult-destinations (get-column adult-rows "DESTINATION")
        adult-destination-counts (count-occurrences adult-destinations)]
    {:most-common-departure (most-common departure-counts)
     :most-common-destination (most-common destination-counts)
     :most-common-age-group (most-common age-group-counts)
     :most-common-adult-destination (most-common adult-destination-counts)}))

(defn print-results [filename]
  (let [analysis (analyze-data filename)]
    (println "Most common departure city:" (first (analysis :most-common-departure)))
    (println "Most common destination city:" (first (analysis :most-common-destination)))
    (println "Most common age group:" (first (analysis :most-common-age-group)))
    (println "Most common destination for adults:" (first (analysis :most-common-adult-destination)))))


(print-results "/Users/dearr/Desktop/ica2sc/src/ica2sc/sales_team_3.csv")


(defn count-tickets [analysis-results]
  (reduce (fn [counts {:keys [group-type flights]}]
            (update counts group-type (fnil + 0) flights))
          {} (mapcat val analysis-results)))




