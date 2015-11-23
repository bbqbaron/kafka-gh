(ns cons.github.github
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [cons.util.produce :refer [p]]
            [clj-kafka.new.producer :as prod]))

(defonce token (slurp "/Users/ericloren/src/streams/clj/cons/token.txt"))

(defn get-from-gh [url]
  (let [response (client/get
          url
          {:headers {"User-Agent" "foo" "Authorization" (format "token %s" token)}})]
    (json/parse-string (:body response) true)))

(defn send-event [e]
  (let [payload (json/generate-string e)]
    @(prod/send p (prod/record "__all__" payload))))

(defn get []
  (let [response (get-from-gh "https://api.github.com/events")]
    (dorun (map send-event (take 2 response)))))

(def tasks
  [
    #(loop []
        (get)
        (Thread/sleep 8000)
        (recur))
  ])
