(ns cons.util.util
  (:require [cons.util.consume :as c]
            [clojure.tools.logging :as log]))

(import '(java.util.concurrent Executors))

(defn thread [tasks]
  (let [pool (Executors/newFixedThreadPool (count tasks))]
    (.invokeAll pool tasks)
    (.shutdown pool)))

(defn map-reduce [fun publish stream]
  (dorun
    (reduce
      (fn [state msg]
        (let [new (fun state (c/body msg))]
          (publish new)
          new))
      {}
      stream)))
