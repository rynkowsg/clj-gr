(ns pl.rynkowski.clj-gr.lang.base64
  (:require
    [pl.rynkowski.clj-gr.lang.bytes :as lb])
  (:import
    (java.io InputStream)
    (java.util Base64 Base64$Decoder Base64$Encoder)))

(def base64-decoder (Base64/getDecoder))
(def base64-encoder (Base64/getEncoder))

(defprotocol DecodableData
  (decode->bytes [data])
  (decode->string [data])
  (decode->stream [data]))

(extend-protocol DecodableData

  byte/1

  (decode->bytes [^bytes data]
    (.decode ^Base64$Decoder base64-decoder ^bytes data))

  (decode->string [^bytes data]
    (-> (decode->bytes data)
        lb/bytes->string))

  (decode->stream [^bytes data]
    (-> (decode->bytes data)
        lb/bytes->stream))

  ; ---

  String

  (decode->bytes [^String data]
    (-> data
        lb/string->bytes
        (decode->bytes)))

  (decode->string [^String data]
    (-> data
        lb/string->bytes
        (decode->string)))

  (decode->stream [^String data]
    (-> data
        lb/string->bytes
        (decode->stream)))

  ; ---

  InputStream

  (decode->bytes [^InputStream data]
    (-> data
        lb/stream->bytes
        (decode->bytes)))

  (decode->string [^InputStream data]
    (-> data
        lb/stream->bytes
        (decode->string)))

  ; TODO: it is not most efficient, should decode directly from stream
  (decode->stream [^InputStream data]
    (-> data
        lb/stream->bytes
        (decode->stream)))

  ; ---

  nil
  (decode->bytes [_] nil)
  (decode->string [_] nil)
  (decode->stream [_] nil))

(comment
  (-> "SGk=" decode->bytes bytes->string)
  (-> "SGk=" string->bytes decode->string)
  (-> "SGk=" decode->string)
  (-> "SGk=" decode->stream slurp))

; ---------------------------

(defprotocol EncodableData
  (encode->bytes [data])
  (encode->string [data])
  (encode->stream [data]))

(extend-protocol EncodableData

  byte/1

  (encode->bytes [^bytes data]
    (.encode ^Base64$Encoder base64-encoder ^bytes data))

  (encode->string [^bytes data]
    (-> data
        (encode->bytes)
        lb/bytes->string))

  (encode->stream [^bytes data]
    (-> data
        (encode->bytes)
        lb/bytes->stream))

  InputStream

  (encode->bytes [^InputStream data]
    (-> data
        lb/stream->bytes
        (encode->bytes)))

  (encode->string [^InputStream data]
    (-> data
        lb/stream->bytes
        (encode->string)))

  ; TODO: it is not most efficient, should encode directly from stream
  (encode->stream [^InputStream data]
    (-> data
        lb/stream->bytes
        (encode->stream)))

  String

  (encode->bytes [^String data]
    (-> data
        lb/string->bytes
        (encode->bytes)))

  (encode->string [^String data]
    (-> data
        (encode->bytes)
        lb/bytes->string))

  (encode->stream [^String data]
    (-> data
        (encode->bytes)
        lb/bytes->stream))

  ; ---

  nil

  (encode->bytes [_] nil)
  (encode->string [_] nil)
  (encode->stream [_] nil))

(comment
  (-> "Hi" encode->bytes lb/bytes->string decode->string)
  (-> "Hi" encode->stream decode->string)
  (-> "Hi" encode->string))
