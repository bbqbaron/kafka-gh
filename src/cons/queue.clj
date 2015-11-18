(ns cons.queue)

(import '(java.util.concurrent Executors))

(use 'clj-kafka.consumer.zk)
(use 'clj-kafka.core)

(def config {"zookeeper.connect" "localhost:2181"
             "group.id" "eventTest"
             "auto.offset.reset" "smallest"
             "auto.commit.enable" "false"})

(defn handle-message [m]
  (println (String. (:value m))))

(def topics [
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

(def topicCounts
  (reduce
    (fn [old new] (assoc old new 1))
    {}
    topics))

(defn handle-stream [stream]
  (dorun (map handle-message (stream-seq stream))))

(defn handle-streams [streams]
  (let [pool (Executors/newFixedThreadPool (count topics))
        tasks (map
          (fn [t]
            (fn []
              (handle-stream (first (get streams t)))))
          topics)]
  (.invokeAll pool tasks)
  (.shutdown pool)))

(with-resource [c (consumer config)]
  shutdown
  (let [streams (create-message-streams c topicCounts)]
    (handle-streams streams)))
