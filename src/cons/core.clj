(ns cons.core
  (:require
    [cons.queue :as queue]
    [cons.aggregate :as aggregate]
    [cons.github :as gh]
    [cons.consume_aggregates :as cons-agg]
    [cons.map :as map]
    [cons.util :refer [thread]])
  (:gen-class))

(set! *warn-on-reflection* true)

(import '(java.util.concurrent Executors))

(defonce tasks
  [
    queue/go
    aggregate/go
    cons-agg/go
    cons-agg/go
    gh/go
    map/go
  ])

(defn run-kafka []
  (thread tasks))

(defn -main []
  (run-kafka))
