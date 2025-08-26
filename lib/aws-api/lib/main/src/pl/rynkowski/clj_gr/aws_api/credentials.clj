(ns pl.rynkowski.clj-gr.aws-api.credentials
  (:require
    [pl.rynkowski.clj-gr.aws-api.internal.utils :refer [try-require]]))

(let [[backend
       CredentialsProvider
       Stoppable
       cached-credentials-with-auto-refresh
       stop
       chain-credentials-provider
       environment-credentials-provider
       system-property-credentials-provider
       profile-credentials-provider
       calculate-ttl
       container-credentials-provider
       instance-profile-credentials-provider
       default-credentials-provider
       basic-credentials-provider
       fetch-async] (or
                      ;; this should always work in babashka
                      (try-require 'pl.rynkowski.clj-gr.aws-api.internal.awyeah.credentials/fns)
                      ;; this needs to be added to the classpath explicitly
                      (try-require 'pl.rynkowski.clj-gr.aws-api.internal.cognitect.credentials/fns))]
  (def backend backend)
  (def CredentialsProvider CredentialsProvider)
  (def Stoppable Stoppable)
  (def cached-credentials-with-auto-refresh cached-credentials-with-auto-refresh)
  (def stop stop)
  (def chain-credentials-provider chain-credentials-provider)
  (def environment-credentials-provider environment-credentials-provider)
  (def system-property-credentials-provider system-property-credentials-provider)
  (def profile-credentials-provider profile-credentials-provider)
  (def calculate-ttl calculate-ttl)
  (def container-credentials-provider container-credentials-provider)
  (def instance-profile-credentials-provider instance-profile-credentials-provider)
  (def default-credentials-provider default-credentials-provider)
  (def basic-credentials-provider basic-credentials-provider)
  (def fetch-async fetch-async))
