(ns cons.event-type.event-type
  (:require
    [cheshire.core :refer [generate-string]]
    [cons.util.consume :as c]
    [cons.util.produce :as p]
    [cons.util.util :refer [map-reduce thread]]
    [clj-kafka.new.producer :as prod]
    [clojure.tools.logging :as log]
    ))

(defn publish [m]
  (p/publish-as "event_types" m))

(defn add-count [state msg]
  (let [type (:type msg)]
    (update-in state [type] (fn [v] (if (nil? v) 1 (+ v 1))))))

(defn go []
  (thread [
    (fn [] (c/consume "aggregator" (partial map-reduce add-count publish) "__all__"))
    (fn [] (c/consume "aggregate-consumer" c/dump-stream "event_types"))
  ]))
