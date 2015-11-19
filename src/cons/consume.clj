(ns cons.consume
  (:require [clj-kafka.consumer.zk :as zk]
            [clj-kafka.core :as core]))

(defn consume [group-id handle topic-name]
  (let [config {"zookeeper.connect" "localhost:2181"
                "group.id" group-id "auto.offset.reset" "largest"}]
    (core/with-resource [c (zk/consumer config)]
      zk/shutdown
      (let [stream (zk/create-message-stream c topic-name)]
        (handle (zk/stream-seq stream))))))