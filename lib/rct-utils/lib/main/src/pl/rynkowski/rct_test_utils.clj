(ns pl.rynkowski.rct-test-utils
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer [is]]
    [com.mjdowney.rich-comment-tests :as rct]
    [com.mjdowney.rich-comment-tests.test-runner :as rct-runner]))

(defn run-rct-test
  [file]
  (let [resource (io/resource file)
        _ (when (nil? resource)
            (throw (ex-info (str "RCT resource not found: " file) {:file file})))
        file' (str (io/file resource))
        ns (rct-runner/try-get-namespace-from-file file')]
    (require ns)
    (rct/run-file-tests! file' (find-ns ns))))
#_(run-rct-test "pl/rynkowski/rct_utils.clj")

(defn run-rct-test-with-is
  [file]
  (let [res (run-rct-test file)]
    (is (= 0 (:error res)))
    (is (= 0 (:fail res)))
    res))
#_(run-rct-test-with-is "pl/rynkowski/rct_utils.clj")
