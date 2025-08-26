(ns pl.rynkowski.clj-gr.aws-api.internal.awyeah.client.api
  (:require
    [com.grzm.awyeah.client.api :refer [client
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

(def fns ['grzm/awyeah-api
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
