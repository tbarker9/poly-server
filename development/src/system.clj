(ns system
  (:require [nimaeskandary.db.interface.sql-db :as sql-db]
            [nimaeskandary.user.interface.sql :as user-repo]
            [nimaeskandary.server.interface.http-kit :as server]
            [nimaeskandary.web.core :as web.core]
            [com.stuartsierra.component :as component]))

(defonce ^:dynamic *system* nil)

(defn dev-system-map
  [_]
  (component/system-map
   :app-db
   (sql-db/create-sql-db
    {:db-spec
     {:dbtype "postgres", :dbname "app", :host "localhost", :port "55432"},
     :pool-config {:username "postgres", :password "password"},
     :migratus-config {:migrations-dir "db/migrations/app_db",
                       :migration-table-name "migrations"}})
   :user-repo (user-repo/create-sql-user-repository)
   :server (server/->HttpKitServer #'web.core/app {:port 9000} true)))

(def dependency-map {:app-db [], :user-repo [:app-db], :server [:user-repo]})

(defn create-dev-system
  []
  (component/system-using (dev-system-map {}) dependency-map))

(defn init-dev-system
  []
  (alter-var-root #'*system* (constantly (create-dev-system))))
(defn start-system [] (alter-var-root #'*system* component/start))
(defn stop-system
  []
  (alter-var-root #'*system* #(when % (component/stop-system %) nil)))
(defn restart-dev-system
  []
  (system/stop-system)
  (system/init-dev-system)
  (system/start-system))
