(ns cons.map
  (:require [cons.consume :as c]
            [cons.produce :as p]
            [cons.github :as gh]
            [clj-kafka.new.producer :as prod]
            [cons.util :refer [thread]]
            [cheshire.core :refer [generate-string parse-string]]))

(defn send-language [m]
    (let [body (c/body m)
          repo-url (:url (:repo body))
          repo (gh/get-from-gh repo-url)
          languages (gh/get-from-gh (:languages_url repo))]
          @(prod/send p/p (prod/record "languages" (generate-string languages)))))

(defn map-to-language [stream]
  (dorun (map send-language stream)))

(defn handle-mapped [stream]
  (dorun (map (fn [m] (println (c/body m))) stream)))

(defn go []
  (thread
    [
      (fn [] (c/consume "map-consumer" handle-mapped "languages"))
      (fn [] (c/consume "mapper" map-to-language "__all__"))
    ]))
