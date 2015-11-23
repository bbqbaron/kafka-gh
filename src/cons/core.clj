(ns cons.core
  (:require
    [cons.saver.saver :as saver]
    [cons.event-type.event-type :as event-type]
    [cons.github.github :as gh]
    [cons.language.language :as language]
    [cons.util.util :refer [thread]])
  (:gen-class))

(set! *warn-on-reflection* true)

(import '(java.util.concurrent Executors))

(defonce tasks
  [
    saver/go
    event-type/go
    gh/go
    language/go
  ])

(defn run-kafka []
  (thread tasks))

(defn -main []
  (run-kafka))
