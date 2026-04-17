(ns pl.rynkowski.lib.csv
  (:require
    [camel-snake-kebab.core :as csk]
    [clojure.data.csv :as csv]
    [medley.core :refer [update-existing]])
  (:import
    (java.io StringWriter)))

;;
;; Map to CSV
;;

(defn- map->row
  "Given `header` (a seq of keys) and a map `m`, return a vector of `m`'s
  values in the order specified by `header`."
  [header m]
  (mapv (partial get m) header))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (map->row [:title :year] {:title "Predator" :year 1987}) ;=> ["Predator" 1987]
  (map->row [:title :year] {:title "Predator" :year 1987 :duration 107}) ;=> ["Predator" 1987]
  #_:rct/test)

(defn maps->table
  "Convert a seq of maps (`maps`) into a 2D vector (table) suitable for CSV-like use.

  Options map:
    :header-keys          ;; ordered keys to extract from each map (defaults to keys of the first map)
    :header-transform-fn  ;; fn applied to each header key for the header row (defaults to [name])

  Returns a vector where the first element is the transformed header row and
  the remaining elements are value rows aligned to that header."
  ([maps]
   (maps->table {} maps))
  ([{:keys [header-keys header-transform-fn]} maps]
   (let [header-transform-fn' (or header-transform-fn name)
         header-keys' (or header-keys (distinct (mapcat keys maps)))
         csv-header (->> header-keys' (mapv header-transform-fn'))
         csv-values (->> maps (mapv #(map->row header-keys' %)))]
     (into [csv-header] csv-values))))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (def movies-maps [{:title "Predator" :year 1987}
                    {:title "Predator 2 " :year 1990}
                    {:title "Predators" :year 2010}
                    {:title "The Predator" :year 2018}])
  (def movies-table [["title" "year"]
                     ["Predator" 1987]
                     ["Predator 2 " 1990]
                     ["Predators" 2010]
                     ["The Predator" 2018]])
  (maps->table {:header-keys [:title :year]
                :header-transform-fn name} movies-maps) ;=> movies-table
  #_:rct/test)

(defn table->csv
  [table]
  (with-open [s (StringWriter.)]
    (csv/write-csv s table)
    (str s)))

(defn maps->csv
  "Render a seq of maps as a CSV string.

  Accepts the same options as `maps->table`:
   :header-keys
   :header-transform-fn

  Uses `clojure.data.csv/write-csv` with its defaults (comma separator, standard quoting).
  Returns the CSV as a String; does not write to disk."
  ([maps]
   (maps->csv {} maps))
  ([opts maps]
   (let [table (maps->table opts maps)]
     (table->csv table))))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (def movies-csv "title,year\nPredator,1987\nPredator 2 ,1990\nPredators,2010\nThe Predator,2018\n")
  (maps->csv {:header-keys [:title :year]
              :header-transform-fn name} movies-maps) ;=> movies-csv
  #_:rct/test)

;;
;; CSV to Map
;;

(defn row->map
  "Translates a single row of values into a map of `colname -> val`, given colnames in `header`."
  [header v]
  (when (not= (count header) (count v))
    (throw (ex-info "Row/header length mismatch" {:header-count (count header) :row-count (count v)})))
  (zipmap header v))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (row->map [:title :year] ["Predator" 1987]) ;=> {:title "Predator" :year 1987}
  (row->map [:title :year :duration] ["Predator" 1987 107]) ;=> {:title "Predator" :year 1987 :duration 107}
  #_:rct/test)

(defn table->maps
  "Takes a sequence of row vectors, as commonly produced by csv parsing libraries, and returns a sequence of
  maps. By default, the first row vector will be interpreted as a header, and used as the keys for the maps.
  However, this and other behaviour are customizable via an optional `opts` map with the following options:

  * `:keyify` - bool; specify whether header/column names should be turned into keywords (default: `true`).
  * `:header` - specify the header to use for map keys, preventing first row of data from being consumed as header.
  * `:structs` - bool; use structs instead of hash-maps or array-maps, for performance boost (default: `false`)."
  ([rows]
   (table->maps {} rows))
  ([{:keys [keyify header structs key-fn] :or {keyify true key-fn csk/->kebab-case-keyword}} rows]
   (let [[header' values] (if (nil? header) [(first rows) (rest rows)] [header rows])
         header'' (if keyify (map key-fn header') header')
         map-fn (if structs
                  (let [s (apply create-struct header'')]
                    (partial apply struct s))
                  (partial row->map header''))]
     (map map-fn values))))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (table->maps movies-table) ;=> movies-maps
  #_:rct/test)

(defn cast-record-with
  [transform-map record]
  (reduce-kv
    (fn [m k f] (update-existing m k f))
    record
    transform-map))

(defn cast-records-with
  [transform-map records]
  (map (fn [r] (cast-record-with transform-map r)) records))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (def records-with-string-amount [{:cur "USD" :date "2020-10-16" :amount "15.42"}
                                   {:cur "USD" :date "2020-10-30" :amount "58.55"}])
  (def records-with-bigdec-amount [{:cur "USD" :date "2020-10-16" :amount 15.42M}
                                   {:cur "USD" :date "2020-10-30" :amount 58.55M}])
  (cast-records-with {:amount #(bigdec %)} records-with-string-amount) ;=> records-with-bigdec-amount
  #_:rct/test)

(defn csv->table [str] (csv/read-csv str))

(defn csv->maps
  [str]
  (-> str
      (csv/read-csv)
      (table->maps)))

^:rct/test
(comment #_((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
  (->> (csv->maps movies-csv)
       (cast-records-with {:year Integer/parseInt})) ;=> movies-maps
  #_:rct/test)
