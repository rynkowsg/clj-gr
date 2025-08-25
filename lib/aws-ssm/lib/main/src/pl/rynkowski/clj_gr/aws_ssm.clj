(ns pl.rynkowski.clj-gr.aws-ssm
  (:require
   [pl.rynkowski.clj-gr.aws-api :as aws]))

(def default-client
  (delay
    (let [client (aws/client-memoized {:api :ssm
                                       :region (System/getenv "AWS_REGION")})]
      (aws/validate-requests client true)
      client)))
#_(-> @default-client aws/ops)
#_(-> @default-client aws/ops keys sort)
#_(-> @default-client aws/doc-str :DescribeParametersRequest)

(defn param
  "Returns the plaintext value of an SSM parameter (supports SecureString)."
  ([{:keys [client name] :or {client @default-client}}]
   (let [resp (aws/invoke! client {:op :GetParameter
                                   :request {:Name name
                                             :WithDecryption true}})]
     (-> resp))))
#_(param {:name "/sample/API_KEY"})

(defn put-param!
  [{:keys [client name value] :or {client @default-client}}]
  (let [resp (aws/invoke! client {:op :PutParameter
                                  :request {:Name name
                                            :Value value
                                            :Type "SecureString"
                                            :Overwrite true}})]
    resp)
  ,)
#_ (put-param! {:name "/sample/API_KEY" :value "TEST_VALUE"})

(defn list-parameters-page
  "Fetch a single DescribeParameters page.
   If next-token is nil, returns the first page."
  [{:keys [client next-token] :or {client @default-client}}]
  (aws/invoke! client
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
           acc'   (into acc params)]
       (if-let [next-token' (:NextToken resp)]
         (recur acc' next-token')
         acc')))))
#_(list-parameters)
