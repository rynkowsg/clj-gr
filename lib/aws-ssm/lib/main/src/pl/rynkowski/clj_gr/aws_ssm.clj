(ns pl.rynkowski.clj-gr.aws-ssm
  (:require
    [camel-snake-kebab.core :as csk]
    [cognitect.aws.client.api :as aws]
    [pl.rynkowski.clj-gr.aws-api.client.api :as gr-aws]))

(def default-client
  (delay
    (let [client (gr-aws/client-memoized {:api :ssm
                                          :region (System/getenv "AWS_REGION")})]
      (aws/validate-requests client true)
      client)))
#_(-> @default-client aws/ops)
#_(-> @default-client aws/ops keys sort)
#_(-> @default-client aws/doc-str :DescribeParametersRequest)

(defn get-param
  [{:keys [client name] :or {client @default-client}}]
  (let [resp (gr-aws/invoke! client {:op :GetParameter
                                     :request {:Name name
                                               :WithDecryption true}})]
    (-> {:status :success :val (some-> resp :Parameter :Value)}
        (vary-meta assoc :aws-resp resp))))
#_(get-param {:name "/sample/API_KEY"})

(defn put-param!
  "Writes an SSM parameter.
   `type` is :secure-string (default) or :string, in any case style,
   e.g. :SecureString or :secure_string."
  [{:keys [client name value type] :or {client @default-client type :secure-string}}]
  (let [resp (gr-aws/invoke! client {:op :PutParameter
                                     :request {:Name name
                                               :Value value
                                               :Type (csk/->PascalCaseString type)
                                               :Overwrite true}})]
    (-> {:status :success}
        (vary-meta assoc :aws-resp resp))))
#_(put-param! {:name "/sample/API_KEY" :value "TEST_VALUE"})
#_(put-param! {:name "/sample/REGION" :value "eu-west-2" :type :string})

(defn del-param!
  [{:keys [client name] :or {client @default-client}}]
  (let [resp (gr-aws/invoke! client {:op :DeleteParameter
                                     :request {:Name name}})]
    resp))
#_(del-param! {:name "/sample/API_KEY"})

(defn copy-param!
  [{:keys [client src-name dst-name] :or {client @default-client}}]
  (let [v (-> (get-param {:client client :name src-name}) :val)]
    (put-param! {:client client :name dst-name :value v})))
#_(copy-param! {:src-name "/sample/API_KEY" :dst-name "/sample/API_KEY_COPY"})

(defn list-parameters-page
  "Fetch a single DescribeParameters page.
   If next-token is nil, returns the first page."
  [{:keys [client next-token] :or {client @default-client}}]
  (gr-aws/invoke! client
                  {:op :DescribeParameters
                   :request (cond-> {}
                                    next-token (assoc :NextToken next-token))}))
#_(list-parameters-page)

(defn list-parameters
  "Return a vector of all SSM parameter metadata across pages."
  ([] (list-parameters {}))
  ([{:keys [client] :or {client @default-client}}]
   (loop [acc []
          next-token nil]
     (let [resp (list-parameters-page {:client client :next-token next-token})
           params (:Parameters resp)
           acc' (into acc params)]
       (if-let [next-token' (:NextToken resp)]
         (recur acc' next-token')
         acc')))))
#_(list-parameters)
