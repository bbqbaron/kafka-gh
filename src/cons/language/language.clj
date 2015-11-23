(ns cons.language.language
  (:require [cons.util.consume :as c]
            [cons.util.produce :as p]
            [cons.github.github :as gh]
            [clj-kafka.new.producer :as prod]
            [cons.util.util :refer [map-reduce, thread]]
            [cheshire.core :refer [generate-string parse-string]]
            [clj-kafka.consumer.zk :as zk]
            [clj-kafka.core :as core]))

(defn consume1 [group-id handle topic-name]
  (let [config {"zookeeper.connect" "localhost:2181"
                "group.id" group-id "auto.offset.reset" "largest"}]
    (core/with-resource [c (zk/consumer config)]
      zk/shutdown
      (let [stream (zk/create-message-stream c topic-name)]
        (handle (zk/stream-seq stream))))))

(defn consume2 [group-id handle topic-name]
  (let [config {"zookeeper.connect" "localhost:2181"
                "group.id" group-id "auto.offset.reset" "largest"}]
    (core/with-resource [c (zk/consumer config)]
      zk/shutdown
      (let [stream (zk/create-message-stream c topic-name)]
        (handle (zk/stream-seq stream))))))

(defn message-to-language [m]
    (let [body (c/body m)
          repo-url (:url (:repo body))
          repo (gh/get-from-gh repo-url)
          languages (gh/get-from-gh (:languages_url repo))]
          (if (> (count (keys languages)) 0)
              (p/publish-as "languages" languages))))

(defn language-mapper [stream]
  (dorun (map message-to-language stream)))

(defn publish [m]
  (p/publish-as "reduced-languages" m))

(defn go []
  (thread
    [
      (fn [] (consume1 "reduce-language" (partial map-reduce merge publish) "languages"))
      (fn [] (consume2 "language-mapper" language-mapper "__all__"))
      (fn [] (c/consume "print-language-summary" c/dump-stream "reduced-languages"))
    ]))
