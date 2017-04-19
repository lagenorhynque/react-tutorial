(ns react-tutorial.prod
  (:require [react-tutorial.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
