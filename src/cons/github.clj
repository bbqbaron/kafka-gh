(ns cons.github
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [cons.produce :refer [p]]
            [clj-kafka.new.producer :as prod]))

(defonce token (slurp "/Users/ericloren/src/streams/clj/cons/token.txt"))

(defn get-from-gh [url]
  (let [response (client/get
          url
          {:headers {"User-Agent" "foo" "Authorization" (format "token %s" token)}})]
    (println (:status response) " from " url)
    (json/parse-string (:body response) true)))

(defn send-event [e]
  (let [payload (json/generate-string e)
          topic (:type e)]
    @(prod/send p (prod/record topic payload)
    @(prod/send p (prod/record "__all__" payload)))))

(defn get []
  (let [response (get-from-gh "https://api.github.com/events")]
    (dorun (map send-event (take 5 response)))))

(defn go []
  (loop []
    (get)
    (Thread/sleep 20000)
    (recur)))
