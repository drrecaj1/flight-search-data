(ns ica2sc.data-analysis-tool-print
  (:require [clojure.java.io :as io]))  ; we need to import the clojure.java.io library as io

(defn read_data [full_path]  ; Function to read data from a file
  (into [] (rest  ; Convert the sequence into a vector, skipping the first line
             (with-open [rdr (io/reader full_path)]
               (reduce conj [] (line-seq rdr))))))

(defn get_children [people]  ; Function to filter a list of people to find those who are under 18 years old in 2023
  (some->> people
           (filter #(< (- 2023 (int (last %))) 18))))

(defn print_people [people budget departure destination]  ; Function to print people's data
  (println people)
  (println "Departure city:" departure)
  (println "Destination city:" destination)
  (println "This is" (cond  ; Determine the group type
                       (not (empty? (get_children people))) "a family"
                       :else "an organized group"))
  (println "Their budget=" budget "per person")
  (println "------------------")
  )

(defn run []  ; Main function
  (let [data_file_name (str "/Users/dearr/Desktop/ica2sc/src/ica2sc/sales_team_3.csv")]  ; Define the data file name
    (if (.exists (io/as-file data_file_name))  ; Check if the file exists
      (let [data (read_data data_file_name)  ; Read the data from the file
            departure (atom "")
            destination (atom "")
            people (atom [])
            last_proposed_budget (atom 0)
            data_count (count data)]
        (loop [row 0]  ; Start a loop
          (if (< row data_count)  ; If the row number is less than the total data count
            (do
              (let [tokens (clojure.string/split (get data row) #",")  ; Split the data row into tokens
                    current_name (first tokens)
                    current_yob (Integer/parseInt (second tokens))
                    current_departure (get tokens 2)
                    current_destination (get tokens 3)
                    current_budget (Integer/parseInt (last tokens))]
                (when (empty? @departure)
                  (reset! departure current_departure))
                (when (empty? @destination)
                  (reset! destination current_destination))

                (reset! last_proposed_budget current_budget)

                (if (or (not= current_departure @departure)  ; If the current departure is not equal to the departure
                        (not= current_destination @destination))  ; Or the current destination is not equal to the destination
                  (do
                    (print_people @people @last_proposed_budget @departure @destination)  ; Print the people's data
                    (reset! departure current_departure)
                    (reset! destination current_destination)
                    (reset! people [[current_name current_yob]])
                    )
                  (do
                    (swap! people conj [current_name current_yob])  ; Add the person to the people
                    ))
                )
              (recur (inc row)))  ; Increment the row number and recur
            )
          )
        (when (> (count @people) 0)
          (print_people @people @last_proposed_budget @departure @destination))  ; Print the people's data if the count of people is greater than 0
        )
      (println "Can't find a data file. Please check team number."))  ; Print an error message if the file doesn't exist
    )
  )

(run)  ; Run the main function
