(ns pl.rynkowski.clj-gr.aws-api.client.shared
  (:require
    [pl.rynkowski.clj-gr.aws-api.internal.utils :refer [try-require]]))

(let [[backend
       http-client
       credentials-provider
       region-provider] (or
                          ;; this should always work in babashka
                          (try-require 'pl.rynkowski.clj-gr.aws-api.internal.awyeah.client.shared/fns)
                          ;; this needs to be added to the classpath explicitly
                          (try-require 'pl.rynkowski.clj-gr.aws-api.internal.cognitect.client.shared/fns))]
  (def backend backend)
  (def http-client http-client)
  (def credentials-provider credentials-provider)
  (def region-provider region-provider))
