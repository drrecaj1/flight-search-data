(ns ica2sc.sales_routines
  (:require [ica2sc.broker :as broker])
  ; TODO replace this link with your engine
  (:require [ica2sc.core :as your_engine])
  )

; TODO SET YOUR TEAM NUMBER: 1-7
(def team_number 3)
(def search_ticket_function your_engine/prepare-travel-plan)
(broker/run team_number search_ticket_function)



