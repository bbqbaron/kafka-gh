(ns cons.produce
  (:require [clj-kafka.new.producer :as prod]
            [cheshire.core :refer [generate-string]]))

(defonce p (prod/producer {"bootstrap.servers" ["localhost:9092"]}
  (prod/string-serializer) (prod/string-serializer)))

(defn publish-as [topic object]
  @(prod/send p (prod/record topic (generate-string object)))
  (println "published as " topic))
