(ns pl.rynkowski.clj-gr.aws-ssm
  (:require
    [camel-snake-kebab.core :as csk]
    [cognitect.aws.client.api :as aws]
    [pl.rynkowski.clj-gr.aws-api.client.api :as gr-aws])
  (:import
    (clojure.lang ExceptionInfo)))

(def default-client
  (delay
    (gr-aws/client* {:api :ssm :validate-requests? true})))
(comment
  (-> @default-client aws/ops)
  (-> @default-client aws/ops keys sort)
  (-> @default-client aws/doc-str :DescribeParametersRequest))

(defn get-param
  "Reads an SSM parameter. Returns nil when the parameter does not exist
   and throws on any other error.
   `with-decryption?` (default true) returns a SecureString decrypted.
   AWS ignores it for String and StringList parameters."
  [{:keys [client name with-decryption?] :or {with-decryption? true}}]
  (let [client' (or client @default-client)]
    (try
      (-> (gr-aws/invoke! client' {:op :GetParameter
                                   :request {:Name name
                                             :WithDecryption with-decryption?}})
          :Parameter)
      (catch ExceptionInfo e
        (when-not (= "ParameterNotFound" (-> e ex-data :origin :cognitect.aws.error/code))
          (throw e))))))
#_(get-param {:name "/sample/API_KEY"})

(defn put-param!
  "Writes an SSM parameter.
   `type` is :secure-string (default) or :string, in any case style,
   e.g. :SecureString or :secure_string.
   `kms-key-id` is the KMS key for a SecureString. Without it AWS uses
   the default aws/ssm key.
   `overwrite?` (default true) replaces an existing parameter. With false,
   writing a parameter that already exists throws."
  [{:keys [client name value type kms-key-id overwrite?] :or {type :secure-string overwrite? true}}]
  (gr-aws/invoke! (or client @default-client)
                  {:op :PutParameter
                   :request (cond-> {:Name name
                                     :Value value
                                     :Type (csk/->PascalCaseString type)
                                     :Overwrite overwrite?}
                                    kms-key-id (assoc :KeyId kms-key-id))}))
#_(put-param! {:name "/sample/API_KEY" :value "TEST_VALUE"})
#_(put-param! {:name "/sample/REGION" :value "eu-west-2" :type :string})

(defn del-param!
  [{:keys [client name]}]
  (let [client' (or client @default-client)
        resp (gr-aws/invoke! client' {:op :DeleteParameter
                                      :request {:Name name}})]
    resp))
#_(del-param! {:name "/sample/API_KEY"})

(defn copy-param!
  "Copies an SSM parameter, keeping its type.
   `kms-key-id` is the KMS key for a SecureString copy. Without it AWS
   uses the default aws/ssm key.
   `overwrite?` (default false) replaces an existing target. With false,
   copying onto a parameter that already exists throws."
  [{:keys [client src-name dst-name kms-key-id overwrite?] :or {overwrite? false}}]
  (let [src (get-param {:client client :name src-name})]
    (put-param! {:client client :name dst-name :value (:Value src) :type (:Type src)
                 :kms-key-id kms-key-id :overwrite? overwrite?})))
#_(copy-param! {:src-name "/sample/API_KEY" :dst-name "/sample/API_KEY_COPY"})

(defn move-param!
  "Moves an SSM parameter by writing a copy with the same type and then
   deleting the source. Throws when the source does not exist. The two
   steps are not atomic, so when the delete fails both parameters remain.
   `kms-key-id` is the KMS key for a SecureString target. Without it AWS
   uses the default aws/ssm key.
   `overwrite?` (default false) replaces an existing target. With false,
   moving onto a parameter that already exists throws."
  [{:keys [client src-name dst-name kms-key-id overwrite?] :or {overwrite? false}}]
  (let [src (get-param {:client client :name src-name})]
    (when-not src
      (throw (ex-info (str "Source parameter not found: " src-name) {:src-name src-name})))
    (when (and (not overwrite?) (get-param {:client client :name dst-name}))
      (throw (ex-info (str "Target parameter already exists: " dst-name) {:dst-name dst-name})))
    (let [resp (put-param! {:client client :name dst-name :value (:Value src) :type (:Type src)
                            :kms-key-id kms-key-id :overwrite? overwrite?})]
      (del-param! {:client client :name src-name})
      resp)))
#_(move-param! {:src-name "/sample/API_KEY_COPY" :dst-name "/sample/API_KEY_MOVED"})

(defn list-params-page
  "Fetch a single DescribeParameters page.
   If next-token is nil, returns the first page."
  [{:keys [client next-token]}]
  (gr-aws/invoke! (or client @default-client)
                  {:op :DescribeParameters
                   ;; 50 is the most DescribeParameters returns in one page, the default is 10
                   :request (cond-> {:MaxResults 50}
                                    next-token (assoc :NextToken next-token))}))
#_(list-params-page)

(defn list-params
  "Return a vector of all SSM parameter metadata across pages."
  ([] (list-params {}))
  ([{:keys [client]}]
   (loop [acc []
          next-token nil]
     (let [resp (list-params-page {:client client :next-token next-token})
           params (:Parameters resp)
           acc' (into acc params)]
       (if-let [next-token' (:NextToken resp)]
         (recur acc' next-token')
         acc')))))
#_(list-params)
