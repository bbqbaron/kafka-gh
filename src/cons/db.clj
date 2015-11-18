(ns cons.db)

(use 'clojure.pprint)

(use 'korma.db)

(defdb db (mysql
  {:db "gh"
    :user "root"
    :password "test"}))

(use 'korma.core)

(defentity commits
  (pk :id)
  (table :commits)
  (database db))

(defn test []
  (->
    commits
    select*
    (fields :id)
    exec
    println))
