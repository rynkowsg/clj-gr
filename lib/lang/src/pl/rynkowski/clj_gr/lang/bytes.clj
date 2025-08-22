(ns pl.rynkowski.clj-gr.lang.bytes
  (:import
    (java.io ByteArrayInputStream InputStream)
    (java.nio.charset StandardCharsets)
    (org.apache.commons.io IOUtils)))

(defn bytes->string [^bytes bytes]
  (String. ^bytes bytes StandardCharsets/UTF_8))

(defn bytes->stream [^bytes bytes]
  (ByteArrayInputStream. bytes))

(defn string->bytes [^String data]
  (.getBytes data StandardCharsets/UTF_8))

(defn stream->bytes [^InputStream stream]
  (IOUtils/toByteArray stream))
