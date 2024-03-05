(ns pl.rynkowski.clj-gr.lang.rct-tests
  (:require
    [clojure.test :refer [deftest]]
    [com.mjdowney.rich-comment-tests.test-runner :as test-runner]))

(deftest rct-tests
  (let [paths (-> (slurp "deps.edn") read-string :paths)]
    (test-runner/run-tests-in-file-tree! :dirs (set paths))))
