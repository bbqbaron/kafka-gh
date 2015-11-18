(ns cons.core
  (:require
    [cons.queue :as queue]
    [cons.aggregate :as aggregate])
  (:gen-class))

(defn -main []
  (aggregate/go)(queue/go))
