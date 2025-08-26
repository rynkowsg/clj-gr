(ns pl.rynkowski.clj-gr.aws-api.internal.utils)

(defn try-require [sym]
  (try (deref (requiring-resolve sym))
       (catch Exception _e
         nil)))
