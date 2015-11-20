(ns cons.util)

(import '(java.util.concurrent Executors))

(defn thread [tasks]
  (let [pool (Executors/newFixedThreadPool (count tasks))]
    (.invokeAll pool tasks)
    (.shutdown pool)))
