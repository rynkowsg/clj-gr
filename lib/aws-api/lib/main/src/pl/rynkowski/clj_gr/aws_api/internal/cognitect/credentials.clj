(ns pl.rynkowski.clj-gr.aws-api.internal.cognitect.credentials
  (:require
    [cognitect.aws.credentials :refer [CredentialsProvider
                                       Stoppable
                                       cached-credentials-with-auto-refresh
                                       stop
                                       chain-credentials-provider
                                       environment-credentials-provider
                                       system-property-credentials-provider
                                       profile-credentials-provider
                                       calculate-ttl
                                       container-credentials-provider
                                       instance-profile-IMDSv2-credentials-provider
                                       default-credentials-provider
                                       basic-credentials-provider
                                       fetch-async]]))

(def fns ['com.cognitect.aws/api
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
          instance-profile-IMDSv2-credentials-provider
          default-credentials-provider
          basic-credentials-provider
          fetch-async])
