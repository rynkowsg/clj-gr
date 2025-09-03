(ns pl.rynkowski.clj-gr.aws-api.credentials
  (:require
    [pl.rynkowski.clj-gr.aws-api.internal.utils :refer [try-require]]))

(let [[backend
       CredentialsProvider
       Stoppable
       cached-credentials-with-auto-refresh
       valid-credentials
       stop
       chain-credentials-provider
       chain-credentials-provider-v2
       environment-credentials-provider
       system-property-credentials-provider
       profile-credentials-provider
       calculate-ttl
       container-credentials-provider
       instance-profile-credentials-provider
       instance-profile-IMDSv2-credentials-provider
       default-credentials-provider
       default-credentials-provider-v2
       basic-credentials-provider
       fetch-async
       assumed-role-credentials-provider
       #_:end] (or
                 ;; this should always work in babashka
                 (try-require 'pl.rynkowski.clj-gr.aws-api.internal.awyeah.credentials/fns)
                 ;; this needs to be added to the classpath explicitly
                 (try-require 'pl.rynkowski.clj-gr.aws-api.internal.cognitect.credentials/fns))]
  (def backend backend)
  (def CredentialsProvider CredentialsProvider)
  (def Stoppable Stoppable)
  (def cached-credentials-with-auto-refresh cached-credentials-with-auto-refresh)
  (def valid-credentials valid-credentials)
  (def stop stop)
  (def chain-credentials-provider chain-credentials-provider)
  (def chain-credentials-provider-v2 chain-credentials-provider-v2)
  (def environment-credentials-provider environment-credentials-provider)
  (def system-property-credentials-provider system-property-credentials-provider)
  (def profile-credentials-provider profile-credentials-provider)
  (def calculate-ttl calculate-ttl)
  (def container-credentials-provider container-credentials-provider)
  (def instance-profile-credentials-provider instance-profile-credentials-provider)
  (when instance-profile-IMDSv2-credentials-provider
    (def instance-profile-IMDSv2-credentials-provider instance-profile-IMDSv2-credentials-provider))
  (def default-credentials-provider default-credentials-provider)
  (def default-credentials-provider-v2 default-credentials-provider-v2)
  (def basic-credentials-provider basic-credentials-provider)
  (def fetch-async fetch-async)
  (def assumed-role-credentials-provider assumed-role-credentials-provider))
