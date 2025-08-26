(ns pl.rynkowski.clj-gr.aws-api.internal.cognitect.client.api
  (:require
    [cognitect.aws.client.api :refer [client
                                      default-http-client
                                      invoke
                                      invoke-async
                                      validate-requests
                                      request-spec-key
                                      response-spec-key
                                      ops
                                      doc-str
                                      doc
                                      stop]]))

(def fns ['com.cognitect.aws/api
          client
          default-http-client
          invoke
          invoke-async
          validate-requests
          request-spec-key
          response-spec-key
          ops
          doc-str
          doc
          stop])
