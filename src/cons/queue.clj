(ns cons.queue
  (:require
    [clj-kafka.consumer.zk :as zk]
    [clj-kafka.core :as core]
    [cheshire.core :refer [parse-string]]
    [cons.db :as db]))

(import '(java.util.concurrent Executors))

(defonce config {"zookeeper.connect" "localhost:2181"
  "group.id" "db-saver" "auto.offset.reset" "largest"})

(defn handle-message [m]
  (db/save-message (parse-string (String. (:value m)) true)))

(defonce topics [
  "CommitCommentEvent"
  "CreateEvent"
  "DeleteEvent"
  "DeploymentEvent"
  "DeploymentStatusEvent"
  "DownloadEvent"
  "FollowEvent"
  "ForkEvent"
  "ForkApplyEvent"
  "GistEvent"
  "GollumEvent"
  "IssueCommentEvent"
  "IssuesEvent"
  "MemberEvent"
  "MembershipEvent"
  "PageBuildEvent"
  "PublicEvent"
  "PullRequestEvent"
  "PullRequestReviewCommentEvent"
  "PushEvent"
  "ReleaseEvent"
  "RepositoryEvent"
  "StatusEvent"
  "TeamAddEvent"
  "WatchEvent"])

(defonce topicCounts
  (reduce
    (fn [old new] (assoc old new 1))
    {}
    topics))

(defn handle-stream [stream]
  (dorun (map handle-message (zk/stream-seq stream))))

(defn handle-streams [streams]
  (let [pool (Executors/newFixedThreadPool (count topics))
        tasks (map
          (fn [t]
            (fn []
              (handle-stream (first (get streams t)))))
          topics)]
    (.invokeAll pool tasks)
    (.shutdown pool)))

(defn go []
  (println "consume event queues")
  (core/with-resource [c (zk/consumer config)]
    zk/shutdown
    (let [streams (zk/create-message-streams c topicCounts)]
      (handle-streams streams))))
