(ns try-guestbook.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [try-guestbook.core-test]))

(doo-tests 'try-guestbook.core-test)

