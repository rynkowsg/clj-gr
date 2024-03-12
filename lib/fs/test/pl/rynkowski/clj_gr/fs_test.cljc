(ns pl.rynkowski.clj-gr.fs_test
  (:require
    [clojure.test :refer [deftest]]
    [com.mjdowney.rich-comment-tests.test-runner :as test-runner]))

(deftest rct-tests
  (test-runner/run-tests-in-file-tree! :dirs #{"src"}))