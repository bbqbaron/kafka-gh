(ns cons.db
  (:require [monger.core :as mg] [monger.collection :as mc] [monger.result :as mr])
  (:import org.bson.types.ObjectId))

(defonce conn (mg/connect))
(defonce db (mg/get-db conn "gh"))
(println conn)

(defn save-message [message]
  (let [id (ObjectId.)
        message-doc (assoc message :_id id)
        coll (:type message-doc)]
    (mc/insert db coll message-doc)))
