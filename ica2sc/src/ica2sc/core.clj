(ns ica2sc.core
  (:require [ica2sc.data-analysis-tool :refer [run store_people read_data get_children]]))


;; data of flights defined as a string
(def flight-data  "Krakov,Warsaw,100,
Hamburg,Berlin,100,
Warsaw,Berlin,300,
Prague,Berlin,200,
Munich,Berlin,100,
Munich,Innsbruck,100,
Vienna,Innsbruck,200,
Vienna,Budapest,300,
Warsaw,Budapest,400,
Zagreb,Budapest,200,
Vienna,Rome,400,
Napoli,Rome,200,
Napoli,Rijeka,100,
Vienna,Prague,200,
Vienna,Rijeka,400,
Rijeka,Zagreb,100,
Vienna,Zagreb,300,
Munich,Zagreb,400,
Innsbruck,Rome,400,
Budapest,Rome,400,
Budapest,Berlin,300,
Prague,Brno,100,
Prague,Budapest,300")

(defn create-flight-list [flight-data] ; created the flight information from flight data
  (reduce (fn [flight-list flight]
            (let [parts (clojure.string/split flight #",")]  ;splits each flight data using a comma as the seperator
              (if (>= (count parts) 3)
                (let [departure (first parts)  ;seperates the information of the cities
                      destination (second parts) ;and prices for flights into parts
                      cost (Integer/parseInt (nth parts 2))]
                  (conj flight-list
                        [departure destination cost]     ;creates the return flights so there are more routes available for clients
                        [destination departure cost]))
                flight-list)))
          []
          (clojure.string/split-lines flight-data)))

(defn create-graph [flight-list]
  (reduce (fn [graph [departure destination cost]]
            (update graph departure conj [destination cost]))
          {}
          flight-list))


(def graph (create-graph (create-flight-list flight-data)))

; function that uses breadh-first search to find the route from destin. to depart.

(defn find-routes [graph start end max-stops max-cost]
  (let [initial-path [{:city start :cost 0}]
        initial-queue [[:initial 0 0]]]
    (loop [queue initial-queue
           routes []]
      (if (empty? queue)
        routes
        (let [[[path-key total-cost stops] & rest-queue] queue
              path (if (= path-key :initial) initial-path path-key)
              current-city (:city (last path))]
          (if (= current-city end)
            (recur rest-queue (conj routes [path total-cost]))
            (let [next-steps (filter (fn [[next-city next-cost]]
                                       (and (<= (+ total-cost next-cost) max-cost)
                                            (not (some #(= next-city (:city %)) path))))
                                     (graph current-city))]
              (recur (reduce (fn [new-queue [next-city next-cost]]
                               (let [new-path (conj path {:city next-city :cost next-cost})
                                     new-total-cost (+ total-cost next-cost)
                                     new-stops (if (= next-city start) 0 (inc stops))]
                                 (conj new-queue [new-path new-total-cost new-stops])))
                             rest-queue
                             next-steps)
                     routes))))))))

; functions to seperate people into groups

(defn is-family [customers]
  (some (fn [customer]
          (let [yob (:yob customer)]
            (and yob (<= (- 2024 yob) 18))))
        customers))


(defn contains-adults [customers]
  (some (fn [customer]
          (let [yob (:yob customer)]
            (and yob (>= (- 2024 yob) 18))))
        customers))


;creating the travel plan for clients based on their input

(defn prepare-travel-plan [departure destination customers]
  ;; Hypotheses:
  ;; 1. Families prefer fewer connections.
  ;; 2. Organized tours are more flexible with connections.
  ;; 3. Special focus on Hamburg as a departure city.
  ;; 4. Rijeka as a popular destination.
  ;; 5. Adults tend to spend more on tickets.

  ;; Determine if the group is a family or an organized group
        (let [family? (is-family customers)
              group-type (if family? "a family" "an organized group")
              groups (ica2sc.data-analysis-tool/run)
              analysis-for-route (first (filter #(and (= (:departure %) departure)
                                                      (= (:destination %) destination)
                                                      (= (:group_type %) group-type)) groups))
        default-budget (if family? 700 1000)
        budget-factor (if (contains-adults customers) 1.2 1.0)  ; Adjusting budget for adult groups
        initial-max-stops (if family? 1 3)]  ; Fewer stops for families, more for organized tours

    (let [budget (* (or (and analysis-for-route (:budget analysis-for-route))
                        default-budget)
                    budget-factor)
          max-stops (-> initial-max-stops
                        (cond-> (= departure "Hamburg") (min 2)
                                (= destination "Rijeka") (min 2)))] ; Special handling for Hamburg and Rijeka

      ;; Search for routes using the adjusted budget and max-stops
      (let [routes (find-routes graph departure destination max-stops budget)]
        (if (empty? routes)
          ;; If no routes found, return a high price indicating no available options
          Integer/MAX_VALUE
          ;; Otherwise, calculate offer price based on the cheapest route or another logic
          (let [cheapest-route (->> routes
                                    (sort-by second)
                                    first)]
            (second cheapest-route)))))))  ; Returns the cost of the cheapest route


(defn parse-customer-data [input]
  (let [parts (clojure.string/split input #",")]
    {:name (first parts) :yob (Integer/parseInt (second parts))}))

(defn collect-customer-data []
  (println "Enter customer data in the correct format (e.g: Jane Doe,2002), new line for each person, and type 'done' when finished:")
  (loop [customers []]
    (let [input (read-line)]
      (if (= "done" input)
        customers
        (recur (conj customers (parse-customer-data input)))))))


;main function for user interaction
(defn main []
  (println "Welcome to your trusted flight search partner where your dream destinations are just a search away!")
  (let [flight-list (create-flight-list flight-data)
        graph (create-graph flight-list)]
    (loop []
      (println "Enter departure city: ")
      (let [departure-city (clojure.string/capitalize (read-line))]
        (when-not (or (= departure-city "Exit") (= departure-city "Restart"))
          (println "Enter destination city: ")
          (let [destination-city (clojure.string/capitalize (read-line))]
            (when-not (or (= destination-city "Exit") (= destination-city "Restart"))
              (let [customers (collect-customer-data)]
                (let [travel-plan (prepare-travel-plan  departure-city destination-city customers)]
                  ;; Check if travel-plan is a number, and handle accordingly
                  (if (number? travel-plan)
                    (println "Total cost per person: " travel-plan)
                    (doseq [plan travel-plan]               ;; If travel-plan is a sequence
                      (println plan)))
                  ; (println "Type 'Restart' to perform another search or 'Exit' to quit: ")
                  ; (let [command (clojure.string/capitalize (read-line))]
                  ; (cond
                  ;(= command "Exit") (do (println "Exiting program.") (System/exit 0))
                    ;(= command "Restart") (recur)
                    )))))))))




(main)






