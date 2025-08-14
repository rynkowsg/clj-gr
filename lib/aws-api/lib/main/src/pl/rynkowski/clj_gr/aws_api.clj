(ns pl.rynkowski.clj-gr.aws-api)

(defn- try-require [sym]
  (try (deref (requiring-resolve sym))
       (catch Exception _e
         nil)))

(let [[backend
       client
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
              (try-require 'pl.rynkowski.clj-gr.aws-api.internal.awyeah/fns)
              ;; this needs to be added to the classpath explicitly
              (try-require 'pl.rynkowski.clj-gr.aws-api.internal.cognitect/fns))]
  (def backend backend)
  (def client client)
  (def invoke invoke)
  (def invoke-async invoke-async)
  (def invoke-async invoke-async)
  (def validate-requests validate-requests)
  (def request-spec-key request-spec-key)
  (def response-spec-key response-spec-key)
  (def ops ops)
  (def doc-str doc-str)
  (def doc doc)
  (def stop stop)
  ,)
