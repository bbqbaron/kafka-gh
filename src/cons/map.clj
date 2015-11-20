(ns cons.map
  (:require [cons.consume :as c]
            [cons.produce :as p]
            [clj-kafka.new.producer :as prod]
            [cons.util :refer [thread]]
            [cheshire.core :refer [generate-string parse-string]]))

(defn handle-message [m]
  (let [lang (:language (:repo (c/body m)))]
    (if ((complement nil?) lang)
      (prod/send p/p (prod/record "mapped" lang))
      )))

(defn handle [stream]
  (dorun (map handle-message stream)))

(defn handle-map [stream]
  (println "read map")
  (dorun (map (fn [m]) stream)))

(defn go []
  (thread
    [
      (fn [] (c/consume "map-consumer" handle-map "mapped"))
      (fn [] (c/consume "mapper" handle "__all__"))
    ]))
