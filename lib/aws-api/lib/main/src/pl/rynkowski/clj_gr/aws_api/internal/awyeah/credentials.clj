(ns pl.rynkowski.clj-gr.aws-api.internal.awyeah.credentials
  (:require
    [com.grzm.awyeah.credentials :refer [CredentialsProvider
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
                                         fetch-async]]))

(def fns ['grzm/awyeah-api
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
          fetch-async])
