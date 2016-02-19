(ns try-guestbook.routes.home
  (:require [try-guestbook.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojurewerkz.elastisch.rest          :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojure.pprint :as pp]))


(defn home-page []
  (layout/render "home.html"))

;(defn es-page [req]
;  "doing something"
;  )

(defn es-page [req]
  ;; performs a term query using a convenience function
  (let [conn (esr/connect "http://127.0.0.1:9200")
        res  (esd/search conn "rehabmeasures" "item" :query (q/match :Diagnosis (str req)))
        n    (esrsp/total-hits res)
        hits (esrsp/hits-from res)
        ids  (esrsp/ids-from res)]
    ;(format "Total hits: %d" n)
    (apply str (map #(get-in % [:_source (keyword "Title of Assessment")] ) hits))
    )
  )



(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/elasticsearch" [] #'es-page)
  (GET "/docs" [] (response/ok (-> "docs/docs.md" io/resource slurp))))

