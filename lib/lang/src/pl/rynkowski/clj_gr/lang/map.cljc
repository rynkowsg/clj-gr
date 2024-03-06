(ns pl.rynkowski.clj-gr.lang.map)

(defn vals->keys
  "Turn over a map, from key->val to val->key"
  [m]
  (reduce (fn [acc [k v]] (assoc acc v k)) {} m))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  "simples case"
  (vals->keys {:a "A" :b "B" :c "C"}) ;; => {"A" :a "B" :b "C" :c}

  "the next one overwrites the previous one"
  (vals->keys {:a "A" :b "B" :c "A"}) ;; =>> (or {"A" :a "B" :b} {"A" :c "B" :b})
  ;; This case is strange. Looks like the behaviour is not deterministic.
  ;; When I run from REPL it is always {"A" :c, "B" :b}, but in tests both Babashka and Clj its {"A" :a "B" :b}.
  #_:comment)
