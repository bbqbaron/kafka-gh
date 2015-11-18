(ns cons.prod
  (:gen-class)
  (:require [clj-http.client :as client] [cheshire.core :as json]))

(use '[clj-kafka.new.producer :as prod])

(defonce p (prod/producer {"bootstrap.servers" ["localhost:9092"]}
  (prod/string-serializer) (prod/string-serializer)))

(defn get []
  (let [
    response
      (client/get
        "https://api.github.com/events"
        {:headers {"User-Agent" "foo" "BasicAuth" "eloren:e31eb234f29a48ee9e78da13495006ce115708e7"}})
    result (json/parse-string (:body response) true)
    firstResultString (json/generate-string (first result))
    topic (:type (first result))
    msgKey (:login (:actor (first result)))]
      @(prod/send p (prod/record topic firstResultString))
      @(prod/send p (prod/record "__all__" firstResultString))))
