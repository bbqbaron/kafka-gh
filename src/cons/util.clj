(ns cons.util
  (:require [cons.consume :as c]
            [clojure.tools.logging :as log]))

(import '(java.util.concurrent Executors))

(defn thread [tasks]
  (let [pool (Executors/newFixedThreadPool (count tasks))]
    (.invokeAll pool tasks)
    (.shutdown pool)))

(defn reduce-counts [publish stream]
  (dorun
    (reduce
      (fn [state msg]
        (println "reduced and publishing")
        (let [new (merge state (c/body msg))]
          (publish new)
          new))
      {}
      stream)))
