(ns pl.rynkowski.clj-gr.fs)

(defmacro with-cwd
  "Execute `body` with a changed working directory."
  [cwd & body]
  `(do
     (let [old-cwd# (System/getProperty "user.dir")
           _a# (System/setProperty "user.dir" (str ~cwd))
           res# ~@body
           _b# (System/setProperty "user.dir" old-cwd#)]
       res#)))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (require '[clojure.string :as str])
  (System/getProperty "user.dir") ; =>> #(str/includes? % "clj-gr")
  (with-cwd "/some-path" (System/getProperty "user.dir")) :=> "/some-path"
 #_:_)
