(ns cons.event-type.event-type
  (:require
    [cons.util.consume :as c]
    [cons.util.produce :as p]
    [cons.util.util :refer [map-reduce thread]]
    [clj-kafka.new.producer :as prod]
    [clojure.tools.logging :as log]
    ))

(defn add-count [state msg]
  (let [type (:type msg)]
    (update-in state [type] (fn [v] (if (nil? v) 1 (+ v 1))))))

(defn go []
  (thread [
    (fn [] (map-reduce "__all__" "event-types" add-count))
    (fn [] (c/dump-stream "event-types"))
  ]))
