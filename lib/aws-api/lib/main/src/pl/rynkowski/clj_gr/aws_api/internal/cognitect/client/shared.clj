(ns pl.rynkowski.clj-gr.aws-api.internal.cognitect.client.shared
  (:require
    [cognitect.aws.client.shared :refer [http-client
                                         credentials-provider
                                         region-provider]]))

(def fns ['com.cognitect.aws/api
          http-client
          credentials-provider
          region-provider])
