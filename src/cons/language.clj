(ns cons.language
  (:require [cons.consume :as c]
            [cons.produce :as p]
            [cons.github :as gh]
            [clj-kafka.new.producer :as prod]
            [cons.util :refer [reduce-counts, thread]]
            [cheshire.core :refer [generate-string parse-string]]))

(defn message-to-language [m]
    (let [body (c/body m)
          repo-url (:url (:repo body))
          repo (gh/get-from-gh repo-url)
          languages (gh/get-from-gh (:languages_url repo))]
          (if (> (count (keys languages)) 0)
              (p/publish-as "languages" (generate-string languages))
              (println "uh"))))

(defn language-mapper [stream]
  (dorun (map message-to-language stream)))

(defn handle-mapped [stream]
  (reduce-counts (partial p/publish-as "reduced-languages") stream))

(defn go []
  (thread
    [
      (fn [] (c/consume "print-language-summary" c/dump-stream "reduced-languages"))
      (fn [] (c/consume "reduce-language" handle-mapped "languages"))
      (fn [] (c/consume "language-mapper" language-mapper "__all__"))
    ]))
