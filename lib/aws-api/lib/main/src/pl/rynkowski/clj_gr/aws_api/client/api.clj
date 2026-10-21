(ns pl.rynkowski.clj-gr.aws-api.client.api
  (:require
    [cognitect.aws.client.api :as aws]))

(def client-memoized
  (memoize
    (fn [opts]
      (aws/client opts))))

(defn invoke!
  "Like cognitect.aws.client.api/invoke, but throws ex-info when an anomaly is returned."
  [client req]
  (let [resp (aws/invoke client req)]
    (if (:cognitect.anomalies/category resp)
      (throw (ex-info (str "AWS " (name (:op req)) " failed: "
                           (or (:cognitect.anomalies/message resp)
                               (some-> resp :cognitect.anomalies/category name)
                               "unknown error"))
                      {:origin resp}))
      resp)))
