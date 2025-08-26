(ns pl.rynkowski.clj-gr.aws-api.internal.awyeah.client.shared
  (:require
    [com.grzm.awyeah.client.shared :refer [http-client
                                           credentials-provider
                                           region-provider]]))

(def fns ['grzm/awyeah-api
          http-client
          credentials-provider
          region-provider])
