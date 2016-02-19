(ns try-guestbook.config
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [try-guestbook.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[try-guestbook started successfully using the development profile]=-"))
   :middleware wrap-dev})
