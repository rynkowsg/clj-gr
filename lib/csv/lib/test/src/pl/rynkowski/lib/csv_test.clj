(ns pl.rynkowski.lib.csv-test
  (:require
    [clojure.test :refer [deftest]]
    [pl.rynkowski.rct-test-utils :refer [run-rct-test]]))

(deftest rct-tests
  (run-rct-test "pl/rynkowski/lib/csv.clj"))
