(ns pl.rynkowski.rct-utils
  (:require
    [com.mjdowney.rich-comment-tests :as rct]
    [medley.core :refer [filter-kv]]
    [taoensso.timbre :as log]))

(defn run-many-ns-tests!
  "Requires each namespace in namespaces (seq of symbols), runs RCTs,
   and returns a map of ns-sym -> {:test :pass :fail :error}."
  [namespaces]
  (let [results (atom {})]
    (doseq [n namespaces]
      (log/info "*** RCTs in" n)
      (require n)
      (let [res (rct/run-ns-tests! n)]
        (swap! results assoc n res)))
    @results))
#_(run-many-ns-tests! ['pl.rynkowski.rct-utils])

(defn assert-no-failures
  [results]
  (let [bad (->> results
                 (filter-kv (fn [_k {:keys [fail error]}]
                              (pos? (+ (or fail 0) (or error 0))))))]
    (when (seq bad)
      (throw (ex-info "Test run failed" {:type :test-run-failed
                                         :failed-count (count bad)
                                         :failed-tests bad})))))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (assert-no-failures {'ns1 {:test 1, :pass 5, :fail 0, :error 0}}) ;=> nil
  (assert-no-failures {'ns1 {:test 1, :pass 5, :fail 1, :error 0}}) ;throws=>> #:error{:message "Test run failed"}
  #_:rct/test)
