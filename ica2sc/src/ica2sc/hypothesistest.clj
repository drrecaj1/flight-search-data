(ns ica2sc.hypothesistest
  (:require [clojure.data.csv :as csv]  ; Importing the clojure.data.csv library as csv
            [clojure.java.io :as io]))  ; Importing the clojure.java.io library as io

(defn read-csv [filename]  ;
  (with-open [reader (io/reader filename)]
    (doall
      (csv/read-csv reader))))

(defn get-column [data column-name]
  (map #(get % column-name) data))

(defn count-occurrences [items]  ; Function to count the occurrences of items
  (frequencies items))

(defn most-common [occurrences]  ; Function to find the most common occurrence
  (first (sort-by val > occurrences)))

(defn calculate-age [birth-year]  ; Function to calculate age based on birth year
  (- (Integer/parseInt (str (java.time.Year/now))) (Integer/parseInt birth-year)))

(defn determine-age-group [age]  ; Function to determine the age group based on age
  (cond
    (< age 18) "youth"
    (and (>= age 18) (< age 35)) "young-adult"
    (and (>= age 35) (< age 60)) "adult"
    :else "senior"))

(defn analyze-data [filename]  ; Function to analyze data from a CSV file
  (let [data (read-csv filename)
        headers (first data)  ; Get the headers from the data
        rows (map (fn [row] (zipmap headers row)) (rest data))  ; Map the headers to the rows
        departures (get-column rows "DEPARTURE")  ; Get the "DEPARTURE" column
        destinations (get-column rows "DESTINATION")  ; Get the "DESTINATION" column
        departure-counts (count-occurrences departures)  ; Count the occurrences of departures
        destination-counts (count-occurrences destinations)  ; Count the occurrences of destinations
        ages (map calculate-age (get-column rows "YOB"))  ; Calculate the ages
        age-groups (map determine-age-group ages)  ; Determine the age groups
        age-group-counts (count-occurrences age-groups)  ; Count the occurrences of age groups
        adult-rows (filter (fn [row] (= (determine-age-group (calculate-age (get row "YOB"))) "adult")) rows)  ; Filter the rows for adults
        adult-destinations (get-column adult-rows "DESTINATION")  ; Get the "DESTINATION" column for adults
        adult-destination-counts (count-occurrences adult-destinations)]  ; Count the occurrences of adult destinations
    {:most-common-departure (most-common departure-counts)
     :most-common-destination (most-common destination-counts)
     :most-common-age-group (most-common age-group-counts)
     :most-common-adult-destination (most-common adult-destination-counts)}))

(defn print-results [filename]
  (let [analysis (analyze-data filename)]
    (println "Most common departure city:" (first (analysis :most-common-departure)))  ; Print the most common departure city
    (println "Most common destination city:" (first (analysis :most-common-destination)))  ; Print the most common destination city
    (println "Most common age group:" (first (analysis :most-common-age-group)))  ; Print the most common age group
    (println "Most common destination for adults:" (first (analysis :most-common-adult-destination)))))  ; Print the most common destination for adults

(print-results "/Users/dearr/Desktop/ica2sc/src/ica2sc/sales_team_3.csv")  ; Print the results for a specific CSV file



