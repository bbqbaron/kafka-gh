(ns cons.core
  (:require
    [cons.queue :as queue]
    [cons.aggregate :as aggregate]
    [cons.prod :as prod]
    [cons.consume_aggregates :as cons-agg])
  (:gen-class))

(import '(java.util.concurrent Executors))

(defonce tasks
  [
    queue/go
    aggregate/go
    cons-agg/go
    prod/go
  ])

(defn run-kafka []
  (let [pool (Executors/newFixedThreadPool (count tasks))]
    (.invokeAll pool tasks)
    (.shutdown pool)))

(defn -main []
  (run-kafka))
