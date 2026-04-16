(ns pl.rynkowski.rct-utils-test
  (:require
    [clojure.test :refer [deftest]]
    [pl.rynkowski.rct-test-utils :refer [run-rct-test]]))

(deftest rct-tests
  (run-rct-test "pl/rynkowski/rct_utils.clj"))
