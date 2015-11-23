(ns cons.language.language
  (:require [cons.util.consume :as c]
            [cons.util.produce :as p]
            [cons.github.github :as gh]
            [clj-kafka.new.producer :as prod]
            [cons.util.util :refer [map-reduce map-stream]]))

(defn message-to-language [m]
    (let [languages (-> m
                      c/body
                      :repo
                      :url
                      gh/get-from-gh
                      :languages_url
                      gh/get-from-gh)]
          (if (> (count (keys languages)) 0)
              languages {})))

(defn language-mapper [stream]
  (dorun (map message-to-language stream)))

(def tasks
  [
    #(map-stream "__all__" "languages" message-to-language)
    #(map-reduce "languages" "reduced-languages" merge)
    #(c/dump-stream "reduced-languages")
  ])
