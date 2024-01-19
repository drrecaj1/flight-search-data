 (ns ica2sc.data-analysis-tool-print
(:require [clojure.java.io :as io])
)

(defn read_data [full_path]
  (into [] (rest
             (with-open [rdr (io/reader full_path)]
               (reduce conj [] (line-seq rdr)))))
  )

(defn get_children [people]
  (some->> people
           (filter #(< (- 2023 (int (last %))) 18)))
  )

(defn print_people [people budget departure destination]
  (println people)
  (println "Departure city:" departure)
  (println "Destination city:" destination)
  (println "This is" (cond
                       (not (empty? (get_children people))) "a family"
                       :else "an organized group"))
  (println "Their budget=" budget "per person")
  (println "------------------")
  )

(defn run []
  (let [data_file_name (str "/Users/dearr/Desktop/ica2sc/src/ica2sc/sales_team_3.csv")]
    (if (.exists (io/as-file data_file_name))
      (let [data (read_data data_file_name)
            departure (atom "")
            destination (atom "")
            people (atom [])
            last_proposed_budget (atom 0)
            data_count (count data)]
        (loop [row 0]
          (if (< row data_count)
            (do
              (let [tokens (clojure.string/split (get data row) #",")
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

                (if (or (not= current_departure @departure)
                        (not= current_destination @destination))
                  (do
                    (print_people @people @last_proposed_budget @departure @destination)
                    (reset! departure current_departure)
                    (reset! destination current_destination)
                    (reset! people [[current_name current_yob]])
                    )
                  (do
                    (swap! people conj [current_name current_yob])
                    ))
                )
              (recur (inc row)))
            )
          )
        (when (> (count @people) 0)
          (print_people @people @last_proposed_budget @departure @destination))
        )
      (println "Can't find a data file. Please check team number."))
    )
  )

(run)