(ns pl.rynkowski.clj-gr.aws-api.client.api
  (:require
    [cognitect.aws.client.api :as aws]
    [pl.rynkowski.awscredentials.aws-api-extras :as awse]))

(defn client
  "Like `cognitect.aws.client.api/client`, with extra options:
   - `:role-arn` assumes that role, with `:session-name` as the optional
     session name. The role is assumed with `:credentials-provider` when
     given, and with the default credentials chain otherwise. The role
     credentials are cached and renewed before they expire.
   - `:validate-requests?` turns on request validation."
  [{:keys [role-arn session-name credentials-provider validate-requests?] :or {validate-requests? false} :as opts}]
  (let [role-cp (when role-arn
                  (awse/default-assumed-role-credentials-provider
                    (cond-> {:role-arn role-arn}
                            session-name (assoc :session-name session-name)
                            credentials-provider (assoc :source-provider credentials-provider))))
        opts' (cond-> (dissoc opts :validate-requests? :role-arn :session-name)
                      role-cp (assoc :credentials-provider role-cp))
        client (aws/client opts')]
    (cond-> client
            validate-requests? (doto (aws/validate-requests true)))))

(def client*
  (memoize (fn [opts] (client opts))))

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
