(ns cons.aggregate
  (:require
    [cheshire.core :refer [generate-string]]
    [cons.consume :as c]
    [clj-time.core :as t]
    [cons.produce :as p]
    [clj-kafka.new.producer :as prod]))

(defn publish [{:keys [counts]}]
  (prod/send p/p (prod/record "aggregates" (generate-string counts))))

(defn add-counts [new-counts state]
  (assoc state :counts new-counts))

(defn add-time [now go state]
  (if go (assoc state :last now) state))

(defn handle [stream]
  (dorun (
    reduce
      (fn [state message]
        (let [event (c/body message)
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
      stream)))

(defn go [] (c/consume "aggregator" handle "__all__"))
