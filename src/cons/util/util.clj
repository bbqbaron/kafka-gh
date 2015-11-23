(ns cons.util.util
  (:require [cons.util.consume :as c]
            [cons.util.produce :as p]
            [clojure.tools.logging :as log]))

(import '(java.util.concurrent Executors))

(defn thread [tasks]
  (let [pool (Executors/newFixedThreadPool (count tasks))]
    (.invokeAll pool tasks)
    (.shutdown pool)))

(defn map-reduce [in out fun]
  (c/consume
    (format "reduce-%s" in)
    (fn [stream]
      (dorun
        (reduce
          (fn [state msg]
            (let [new (fun state (c/body msg))]
              (p/publish-as out new)
              new))
          {}
          stream)))
    in))

(defn map-stream [in out fun]
  (c/consume
    (format "map-%s" in)
    (fn [stream]
      (dorun
        (map
          (fn [msg]
            (let [result (fun msg)]
              (p/publish-as out result)
            ))
          stream)))
    in))
