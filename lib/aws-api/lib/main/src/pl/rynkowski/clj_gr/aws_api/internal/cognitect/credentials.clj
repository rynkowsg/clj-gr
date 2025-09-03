(ns pl.rynkowski.clj-gr.aws-api.internal.cognitect.credentials
  (:require
    [cognitect.aws.client.api :as aws]
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

(defn assumed-role-credentials-provider
  "Returns a CredentialsProvider that calls STS:AssumeRole using `source-provider`.
   Wrapped with cached auto-refresh so creds renew before expiration."
  [{:keys [source-provider role-arn http-client session-name]
    :or {http-client (aws/default-http-client)
         session-name (str (System/currentTimeMillis))}}]
  (let [sts-request (cond-> {:api :sts
                             :http-client http-client}
                            source-provider (assoc :credentials-provider source-provider))
        sts (aws/client sts-request)
        base (reify CredentialsProvider
               (fetch [_]
                 (let [resp (aws/invoke sts {:op :AssumeRole
                                             :request {:RoleArn role-arn
                                                       :RoleSessionName session-name}})
                       {:keys [AccessKeyId SecretAccessKey SessionToken Expiration]} (:Credentials resp)
                       res {:aws/access-key-id AccessKeyId
                            :aws/secret-access-key SecretAccessKey
                            :aws/session-token SessionToken
                            ;; ttl drives auto-refresh
                            :cognitect.aws.credentials/ttl (calculate-ttl {:Expiration Expiration})}]
                   res)))]
    (cached-credentials-with-auto-refresh base)))

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
          fetch-async
          assumed-role-credentials-provider])
