(ns pl.rynkowski.clj-gr.bb
  (:require
    [clojure.string :as str]))

(defn print-exec
  ([command] (print-exec command nil))
  ([command args]
   (let [shell? (= (first command) 'shell)
         full-cmd (concat command args)]
     (println "CMD:" (->> (if shell? (rest full-cmd) full-cmd)
                          (str/join " ")))
     (println "---")
     (eval full-cmd))))
#_(print-exec '(clojure "-M:test:tool/kaocha -m kaocha.runner --config-file tests.edn") *command-line-args*)
#_(print-exec '(shell "bb tool:kaocha:clj :unit"))

(defn deps-map
  [{:keys [path aliases]
    :or {aliases []}}]
  (let [deps-edn (-> path (slurp) (read-string))
        main-deps (-> deps-edn :deps)
        main-paths (-> deps-edn :paths)
        aliases-contents (->> aliases (map #(get-in deps-edn [:aliases %])))
        aliases-extra-deps (->> aliases-contents (map :extra-deps) (filter some?))
        aliases-extra-paths (->> aliases-contents (map :extra-paths) (filter some?))
        deps-map {:deps (apply merge main-deps aliases-extra-deps)
                  :paths (apply concat main-paths aliases-extra-paths)}]
    deps-map))
#_(deps-map {:path "deps.edn" :aliases [:repl :lib/all]})
