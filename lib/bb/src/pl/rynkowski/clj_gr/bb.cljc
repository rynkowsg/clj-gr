(ns pl.rynkowski.clj-gr.bb
  (:require
    [clojure.string :as str]))

(defn print-exec
  ([command] (print-exec command nil))
  ([command args]
   (let [full-cmd (concat command args)]
     (println "CMD:" (str/join " " full-cmd))
     (println "---")
     (eval full-cmd))))
