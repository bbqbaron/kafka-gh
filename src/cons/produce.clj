(ns cons.produce
  (:require [clj-kafka.new.producer :as prod]))

(defonce p (prod/producer {"bootstrap.servers" ["localhost:9092"]}
  (prod/string-serializer) (prod/string-serializer)))
