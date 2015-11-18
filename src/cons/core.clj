(ns cons.core (:gen-class))

(require '[cons.db :as db])

(db/test)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
