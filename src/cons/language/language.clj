(ns cons.language.language
  (:require [cons.util.consume :as c]
            [cons.util.produce :as p]
            [cons.github.github :as gh]
            [clj-kafka.new.producer :as prod]
            [cons.util.util :refer [map-reduce, map-stream, thread]]))

(defn message-to-language [m]
    (let [body (c/body m)
          repo-url (:url (:repo body))
          repo (gh/get-from-gh repo-url)
          languages (gh/get-from-gh (:languages_url repo))]
          (if (> (count (keys languages)) 0)
              (p/publish-as "languages" languages))))

(defn language-mapper [stream]
  (dorun (map message-to-language stream)))

(defn go []
  (thread
    [
      (fn [] (map-reduce "languages" "reduced-languages" merge))
      (fn [] (map-stream "__all__" "languages" message-to-language))
      (fn [] (c/dump-stream "reduced-languages"))
    ]))
