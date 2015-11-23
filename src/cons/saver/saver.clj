(ns cons.saver.saver
  (:require
    [clj-kafka.core :as core]
    [cheshire.core :refer [parse-string]]
    [cons.util.db :as db]
    [cons.util.consume :as c]))

(defn handle-message [m]
  (db/save-message (c/body m)))

(defn handle-stream [stream]
  (dorun (map handle-message stream)))

(def tasks [
  (fn [] (c/consume "db-saver" handle-stream "__all__"))
  ])
