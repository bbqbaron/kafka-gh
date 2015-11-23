(ns cons.saver.saver
  (:require
    [clj-kafka.consumer.zk :as zk]
    [clj-kafka.core :as core]
    [cheshire.core :refer [parse-string]]
    [cons.util.db :as db]
    [cons.util.consume :as c]))

(defn handle-message [m]
  (db/save-message (parse-string (String. (:value m)) true)))

(defn handle-stream [stream]
  (dorun (map handle-message stream)))

(defn go []
  (c/consume ["db-saver" handle-stream "__all__"]))
