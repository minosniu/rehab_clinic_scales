(ns try-guestbook.config
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[try-guestbook started successfully]=-"))
   :middleware identity})
