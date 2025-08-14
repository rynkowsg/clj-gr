(ns pl.rynkowski.clj-gr.aws-ssm
  (:require
   [pl.rynkowski.aws-api :as aws]))

(def client
  (delay
   (aws/client {:api :ssm})))

(defn param
  "Returns the plaintext value of an SSM parameter (supports SecureString)."
  ([name] (param @client name))
  ([client name]
   (let [resp (aws/invoke client {:op :GetParameter
                                  :request {:Name name
                                            :WithDecryption true}})]
     (if (:cognitect.anomalies/category resp)
       (throw (ex-info "GetParameter failed" resp))
       (get-in resp [:Parameter :Value])))))

#_ (param "/sample/API_KEY")
