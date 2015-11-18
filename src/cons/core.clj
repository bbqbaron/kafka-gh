(ns cons.core
  (:require [cons.queue :as queue])
  (:gen-class))

(defn -main []
  (queue/go))
