(ns try-guestbook.app
  (:require [try-guestbook.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
