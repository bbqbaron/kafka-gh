(ns cons.aggregate
  (:require
    [cheshire.core :refer [generate-string]]
    [cons.consume :as c]
    [cons.produce :as p]
    [cons.util :refer [reduce-counts]]
    [clj-kafka.new.producer :as prod]
    [clojure.tools.logging :as log]))

(defn publish [{:keys [counts]}]
  (p/publish-as "aggregates" counts))

(defn go [] (c/consume "aggregator" (partial reduce-counts publish) "__all__"))
