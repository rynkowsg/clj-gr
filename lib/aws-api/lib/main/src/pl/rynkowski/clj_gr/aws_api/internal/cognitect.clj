(ns pl.rynkowski.clj-gr.aws-api.internal.cognitect
  (:require
    [cognitect.aws.client.api :as aws]))

(def client aws/client)

(def invoke aws/invoke)

(def invoke-async aws/invoke-async)

(def validate-requests aws/validate-requests)

(def request-spec-key aws/request-spec-key)

(def response-spec-key aws/response-spec-key)

(def ops aws/ops)

(def doc-str aws/doc-str)

(def doc aws/doc)

(def stop aws/stop)

(def fns ['com.cognitect.aws/api
          client
          invoke
          invoke-async
          validate-requests
          request-spec-key
          response-spec-key
          ops
          doc-str
          doc
          stop])
