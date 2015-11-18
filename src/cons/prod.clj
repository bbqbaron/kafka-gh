(ns cons.prod
  (:require [clj-http.client :as client] [cheshire.core :as json] [clj-kafka.new.producer :as prod]))

(defonce p (prod/producer {"bootstrap.servers" ["localhost:9092"]}
  (prod/string-serializer) (prod/string-serializer)))

(defn get []
  (println "getting")
  (let [
    response
      (client/get
        "https://api.github.com/events"
        {:headers {"User-Agent" "foo" "Authorization" "token db9b6ff2ebd4ee332b9f78f9e5923f2ad88a72c2"}})
    result (json/parse-string (:body response) true)
    firstResultString (json/generate-string (first result))
    topic (:type (first result))
    msgKey (:login (:actor (first result)))]
      (println response)
      @(prod/send p (prod/record topic firstResultString))
      @(prod/send p (prod/record "__all__" firstResultString))))

(defn go []
  (loop []
    (get)
    (Thread/sleep 1000)
    (recur)))
