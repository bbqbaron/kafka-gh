(ns cons.consume_aggregates
  (:require [cons.consume :as c]
            [cheshire.core :refer [parse-string]]))

(defn handle [msg])

(defn go [] (c/consume "aggregate-consumer" #(dorun (map handle %)) "aggregates"))
