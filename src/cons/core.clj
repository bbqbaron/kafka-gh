(ns cons.core
  (:require
    [cons.queue :as queue]
    [cons.aggregate :as aggregate]
    [cons.prod :as prod]
    [cons.consume_aggregates :as cons-agg]
    [cons.map :as map]
    [cons.util :refer [thread]])
  (:gen-class))

(import '(java.util.concurrent Executors))

(defonce tasks
  [
    queue/go
    aggregate/go
    cons-agg/go
    cons-agg/go
    prod/go
    map/go
  ])

(defn run-kafka []
  (thread tasks))

(defn -main []
  (run-kafka))
