(ns cons.prod
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [cons.produce :refer [p]]
            [clj-kafka.new.producer :as prod]))

(defonce token (slurp "/Users/ericloren/src/streams/clj/cons/token.txt"))

(defn get []
  (println "getting")
  (let [
    response
      (client/get
        "https://api.github.com/events"
        {:headers {"User-Agent" "foo" "Authorization" (format "token %s" token)}})
    result (json/parse-string (:body response) true)
    firstResultString (json/generate-string (first result))
    topic (:type (first result))]
      (println (:status response))
        @(prod/send p (prod/record topic firstResultString))
        @(prod/send p (prod/record "__all__" firstResultString))))

(defn go []
  (loop []
    (get)
    (Thread/sleep 3000)
    (recur)))
