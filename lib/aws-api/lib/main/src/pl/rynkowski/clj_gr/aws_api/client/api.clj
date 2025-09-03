(ns pl.rynkowski.clj-gr.aws-api.client.api
  (:require
    [pl.rynkowski.clj-gr.aws-api.internal.utils :refer [try-require]]))

(let [[backend
       client
       default-http-client
       invoke
       invoke-async
       validate-requests
       request-spec-key
       response-spec-key
       ops
       doc-str
       doc
       stop] (or
               ;; this should always work in babashka
               (try-require 'pl.rynkowski.clj-gr.aws-api.internal.awyeah.client.api/fns)
               ;; this needs to be added to the classpath explicitly
               (try-require 'pl.rynkowski.clj-gr.aws-api.internal.cognitect.client.api/fns))]
  (def backend backend)
  (def client client)
  (def default-http-client default-http-client)
  (def invoke invoke)
  (def invoke-async invoke-async)
  (def validate-requests validate-requests)
  (def request-spec-key request-spec-key)
  (def response-spec-key response-spec-key)
  (def ops ops)
  (def doc-str doc-str)
  (def doc doc)
  (def stop stop))

(def client-memoized
  (memoize
    (fn [opts]
      (client opts))))

(defn invoke!
  "Like cognitect.aws.client.api/invoke, but throws ex-info when an anomaly is returned."
  [client req]
  (let [resp (invoke client req)]
    (if (:cognitect.anomalies/category resp)
      (throw (ex-info (str "AWS " (name (:op req)) " failed: "
                           (or (:cognitect.anomalies/message resp)
                               (some-> resp :cognitect.anomalies/category name)
                               "unknown error"))
                      {:origin resp}))
      resp)))
