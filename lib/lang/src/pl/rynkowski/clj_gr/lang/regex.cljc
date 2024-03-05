(ns pl.rynkowski.clj-gr.lang.regex)

(defn named-groups
  "Looks for groups named with pattern like (?<name>...) and returns map (name, value) for each named group found."
  [regex text]
  (let [named-groups (->> (re-seq #"\?\<([a-zA-Z0-9]+)\>" (str regex)) (map second))
        matcher (re-matcher regex text)]
    (when (.matches matcher)
      (->> named-groups
           (map (fn [^String word] [(keyword word) (.group matcher word)]))
           (into {})))))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (def date-regex #"^(?<year>(?:19|20)\d\d)\-(?<month>0?[1-9]|1[012])\-(?<day>[12][0-9]|3[01]|0?[1-9])$")
  (def date-time-regex #"^(?<year>(?:19|20)\d\d)\-(?<month>0?[1-9]|1[012])\-(?<day>[12][0-9]|3[01]|0?[1-9])T(?<hour>[01][0-9]|2[0-3]):(?<min>[0-5][0-9])(:(?<sec>[0-5][0-9]))?$")
  (def month-xml-regex #".*(?<year>\d{4})-(?<month>.{2})-monthly.xml")

  ;; regular matches when I use groups
  (re-matches date-regex "2020-01-01") ;; => ["2020-01-01" "2020" "01" "01"]
  (re-matches date-time-regex "2020-01-01T12:25:32") ;; => ["2020-01-01T12:25:32" "2020" "01" "01" "12" "25" ":32" "32"]
  (re-matches month-xml-regex "/some/path/2017-06-monthly.xml") ;; => ["/some/path/2017-06-monthly.xml" "2017" "06"]

  ;; matching with named-groups
  (named-groups date-regex "2020-01-01") ;; => {:year "2020" :month "01" :day "01"}
  (named-groups date-time-regex "2020-01-01T12:25:32") ;; => {:year "2020" :month "01" :day "01" :hour "12" :min "25" :sec "32"}
  (named-groups date-time-regex "2020-01-01T12:25") ;; => {:year "2020" :month "01" :day "01" :hour "12" :min "25" :sec nil}
  (named-groups month-xml-regex "/some/path/2017-06-monthly.xml") ;; => {:year "2017" :month "06"}
  #_:comment)
