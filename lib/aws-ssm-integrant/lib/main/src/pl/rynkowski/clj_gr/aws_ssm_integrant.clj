(ns pl.rynkowski.clj-gr.aws-ssm-integrant
  (:require
    [clojure.spec.alpha :as s]
    [integrant.core :as ig]
    [pl.rynkowski.clj-gr.aws-ssm :as aws-ssm]))

(s/def ::client any?)

(defmethod ig/init-key ::client [_ _]
  @aws-ssm/client)
