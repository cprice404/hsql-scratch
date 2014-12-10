(ns hsql-scratch.server
  (:import (org.hsqldb Server)
           (java.sql DriverManager)
           (org.hsqldb.util DatabaseManagerSwing)))

(def server (atom nil))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HSQL scratch code
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; To use:
;;
;; * Start up 2 separate REPLs
;; * Load this namespace in both
;; * Run `start-server` in one of them
;; * Run `run-sql` in the second REPL
;; * Run `run-gui` in the second REPL; use 'jdbc:hsqldb:hsql://localhost/foo' as
;;   the jdbc URL.

(defn start-server
  []
  (reset! server
          (doto (Server.)
            (.setLogWriter nil)
            (.setDatabaseName 0 "foo")
            (.setDatabasePath 0 "file:foo")
            (.start))))

(defn stop-server
  []
  (.stop @server))

(defn execute
  [conn stmt]
  (-> conn
      (.prepareStatement stmt)
      (.execute)))

(defn execute-query
  [conn stmt]
  (-> conn
      (.prepareStatement stmt)
      (.executeQuery)))

(defn run-sql
  []
  (Class/forName "org.hsqldb.jdbcDriver")
  (let [conn (DriverManager/getConnection "jdbc:hsqldb:hsql://localhost/foo" "sa" "")]
    (execute conn "drop table bar if exists;")
    (execute conn "create table bar (id int, value varchar(20) not null);")
    (execute conn "insert into bar values (1, 'foofoo');")

    (let [result (execute-query conn "select * from bar;")]
      (.next result)
      (println (format "id: '%s', value: '%s'" (.getInt result 1) (.getString result 2))))))

(defn run-gui
  []
  (DatabaseManagerSwing/main (into-array String [])))