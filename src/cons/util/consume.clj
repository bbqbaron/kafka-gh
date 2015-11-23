(ns cons.util.consume
  (:require [clj-kafka.consumer.zk :as zk]
            [clj-kafka.core :as core]
            [cheshire.core :refer [parse-string]]))

(defn consume [group-id handle topic-name]
  (let [config {"zookeeper.connect" "localhost:2181"
                "group.id" group-id "auto.offset.reset" "largest"}]
    (core/with-resource [c (zk/consumer config)]
      zk/shutdown
      (let [stream (zk/create-message-stream c topic-name)]
        (handle (zk/stream-seq stream))))))

(defn body [m]
  (parse-string (String. (:value m)) true))

(defn dump-stream [name]
  (consume
    (format "%s-dumper" name)
    (fn [s] (dorun (map (fn [m] (println (body m))) s)))
    name))
