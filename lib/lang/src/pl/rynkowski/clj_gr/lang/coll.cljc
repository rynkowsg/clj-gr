(ns pl.rynkowski.clj-gr.lang.coll)

(defn some-insight
  "some but except returning only the result, returns also the element"
  [pred coll]
  (some (fn [el]
          (when-let [result (pred el)] {:el el :res result})) coll))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (require '[pl.rynkowski.clj-gr.lang.regex :refer [named-groups]])
  (def regexes {:github #"^(?<dir>[^@]*)/(?<type>@github)/(?<user>[^@/]*)/(?<name>[^@/]*)(@(?<coordinate>[^/]*))?/(?<path>.*)$"
                :https #"^(?<dir>[^@]*)/(?<type>@https)/(?<path>.*)$"})
  (some-insight #(named-groups (val %) "../../lib/@github/rynkowsg/shellpack@241kab43/lib/trap.bash") regexes) ;=>> map?
  (some-insight #(named-groups (val %) "../../lib/@https/raw.githubusercontent.com/rynkowsg/shellpack/lib/trap.bash") regexes) ;=>> map?
  (some-insight #(named-groups (val %) "../../lib/https/raw.githubusercontent.com/rynkowsg/shellpack/lib/trap.bash") regexes) ;=> nil
  #_:comment)
