(ns ica2sc.data-analysis-tool
  (:require [clojure.java.io :as io]))  ; Importing the clojure.java.io library as io

(defn read_data [full_path]  ; Function to read data from a file
  (into [] (rest  ; Convert the sequence into a vector, skipping the first line
             (with-open [rdr (io/reader full_path)]  ; Open the file
               (reduce conj [] (line-seq rdr))))))  ; Read lines into a sequence

(defn get_children [people]  ; Function to filter a list of people to find those who are under 18 years old in 2023
  (some->> people
           (filter #(< (- 2023 (int (last %))) 18))))

(defn store_people [people budget departure destination]  ; Function to store people's data
  {:people people
   :departure departure
   :destination destination
   :group_type (if (empty? (get_children people)) "an organized group" "a family")  ; Determine the group type
   :budget budget})

(defn run []  ; Main function
  (let [data_file_name (str "/Users/dearr/Desktop/ica2sc/src/ica2sc/sales_team_3.csv")]  ; Define the data file name
    (if (.exists (io/as-file data_file_name))  ; Check if the file exists
      (let [data (read_data data_file_name)  ; Read the data from the file
            departure (atom "")
            destination (atom "")
            people (atom [])
            last_proposed_budget (atom 0)
            data_count (count data)
            groups (atom [])]
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
                  (reset! departure current_departure))  ; Reset the departure if it's empty
                (when (empty? @destination)
                  (reset! destination current_destination))  ; Reset the destination if it's empty

                (reset! last_proposed_budget current_budget)  ; Reset the last proposed budget

                (if (or (not= current_departure @departure)  ; If the current departure is not equal to the departure
                        (not= current_destination @destination))  ; Or the current destination is not equal to the destination
                  (do
                    (swap! groups conj (store_people @people @last_proposed_budget @departure @destination))  ; Add the people to the groups
                    (reset! departure current_departure)  ; Reset the departure
                    (reset! destination current_destination)  ; Reset the destination
                    (reset! people [[current_name current_yob]])  ; Reset the people
                    )
                  (do
                    (swap! people conj [current_name current_yob])  ; Add the person to the people
                    ))
                )
              (recur (inc row)))  ; Increment the row number and recur
            )
          )
        (when (> (count @people) 0)
          (swap! groups conj (store_people @people @last_proposed_budget @departure @destination)))  ; Add the people to the groups if the count of people is greater than 0
        @groups)  ; Return the groups
      (do
        (println "Can't find a data file. Please check team number.")
        nil)  ; Return nil if the file doesn't exist
      )
    ) )

(run)  ; Run the main function
