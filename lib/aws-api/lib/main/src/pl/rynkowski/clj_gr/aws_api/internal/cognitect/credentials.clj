(ns pl.rynkowski.clj-gr.aws-api.internal.cognitect.credentials
  (:require
    [cognitect.aws.credentials :refer [CredentialsProvider
                                       fetch
                                       Stoppable
                                       -stop
                                       cached-credentials-with-auto-refresh
                                       valid-credentials
                                       stop
                                       chain-credentials-provider
                                       environment-credentials-provider
                                       system-property-credentials-provider
                                       profile-credentials-provider
                                       calculate-ttl
                                       container-credentials-provider
                                       instance-profile-credentials-provider
                                       instance-profile-IMDSv2-credentials-provider
                                       default-credentials-provider
                                       basic-credentials-provider
                                       fetch-async]]))

(defn chain-credentials-provider-v2
  [{:keys [providers valid-provider-cached?] :or {valid-provider-cached? true}}]
  (let [cached-provider (atom nil)]
    (reify
      CredentialsProvider
      (fetch [_]
        (valid-credentials
          (if (and valid-provider-cached? @cached-provider)
            (fetch @cached-provider)
            (some (fn [provider]
                    (when-let [creds (fetch provider)]
                      (reset! cached-provider provider)
                      creds))
                  providers))))
      Stoppable
      (-stop [_] (run! -stop providers)))))

(defn default-credentials-provider-v2
  [{:keys [http-client valid-provider-cached?]}]
  (chain-credentials-provider-v2
    {:providers [(environment-credentials-provider)
                 (system-property-credentials-provider)
                 (profile-credentials-provider)
                 (container-credentials-provider http-client)
                 (instance-profile-IMDSv2-credentials-provider http-client)
                 (instance-profile-credentials-provider http-client)]
     :valid-provider-cached? valid-provider-cached?}))

(def fns ['com.cognitect.aws/api
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
          fetch-async])
