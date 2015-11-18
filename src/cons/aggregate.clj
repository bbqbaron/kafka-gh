(ns cons.aggregate
  (:require
    [cheshire.core :refer [generate-string parse-string]]
    [cons.queue :refer [config]]
    [clj-kafka.core :as core]
    [clj-kafka.consumer.zk :as zk]
    [clj-kafka.new.producer :as prod]
    [clj-time.core :as t]))

(defonce p (prod/producer {"bootstrap.servers" ["localhost:9092"]}
  (prod/string-serializer) (prod/string-serializer)))

(defn publish [{:keys [counts]}]
  (prod/send p (prod/record "aggregates" (generate-string counts))))

(defn add-counts [new-counts state]
  (assoc state :counts new-counts))

(defn add-time [now go state]
  (if go (assoc state :last now) state))

(defn handle [stream]
  (dorun (
    reduce
      (fn [state {:keys [value]}]
        (let [event (parse-string (String. value) true)
            now (t/now)
            elapsed (t/interval (:last state) now)
            go (> (t/in-seconds elapsed) 10)
            counts (:counts state)
            new-counts (update counts (:type event) (fn [x] (+ 1 (or x 0))))
            new-state (add-counts new-counts (add-time now go state))]
          (if go (publish new-state))
          new-state))
    (let [time (t/now)]
      {:last time :counts {}})
    (zk/stream-seq stream))))

(defn go []
  (core/with-resource [c (zk/consumer config)]
    zk/shutdown
    (let [stream (zk/create-message-stream c "__all__")]
      (handle stream))))
