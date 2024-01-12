(ns pl.rynkowski.pom
  (:require
   [clojure.data.xml :as xml]
   [clojure.string :as str]
   [clojure.tools.build.tasks.write-pom :as write-pom]
   [hyperfiddle.rcf :refer [tests]])
  (:import
   (java.util Date)))

(xml/alias-uri 'pom "http://maven.apache.org/POM/4.0.0")

(defn gen-scm [scm]
  (let [{:keys [connection developerConnection tag url]} scm]
    (when scm
      [::pom/scm
       (when connection [::pom/connection connection])
       (when developerConnection [::pom/developerConnection developerConnection])
       (when tag [::pom/tag tag])
       (when url [::pom/url url])])))

(defn gen-pom-xml
  [params]
  (let [{:keys [basis group-id artifact-id version name repos scm indent?] :or {indent? true}} params
        {:keys [libs]} basis
        repos' (or repos (->> (:mvn/repos basis) (remove #(= "https://repo1.maven.org/maven2/" (-> % val :url)))))
        deps (#'write-pom/libs->deps libs)
        res (xml/sexp-as-element
             [::pom/project
              {:xmlns "http://maven.apache.org/POM/4.0.0"
               (keyword "xmlns:xsi") "http://www.w3.org/2001/XMLSchema-instance"
               (keyword "xsi:schemaLocation") "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"}
              [::pom/modelVersion "4.0.0"]
              [::pom/packaging "jar"]
              [::pom/groupId group-id]
              [::pom/artifactId artifact-id]
              [::pom/version version]
              [::pom/name (or name artifact-id)]
              (#'write-pom/gen-repos repos')
              (#'write-pom/gen-deps deps)
              (gen-scm scm)])]
    (if indent?
      (xml/indent-str res)
      (xml/emit-str res))))

(tests
 (= (gen-pom-xml {:artifact-id "clj-sample"
                  :group-id "io.github.rynkowsg"
                  :version "1.0.0"
                  :indent? false
                  :scm {:tag "1.0.0"}
                  :basis {:libs {'org.clojure/clojure {:mvn/version "1.11.1"}}}})
    (->> "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
          <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">
            <modelVersion>4.0.0</modelVersion>
            <packaging>jar</packaging>
            <groupId>io.github.rynkowsg</groupId>
            <artifactId>clj-sample</artifactId>
            <version>1.0.0</version>
            <name>sample-lib</name>
            <repositories/>
            <dependencies>
              <dependency>
                <groupId>org.clojure</groupId>
                <artifactId>clojure</artifactId>
                <version>1.11.1</version>
              </dependency>
            </dependencies>
            <scm>
              <tag>1.0.0</tag>
            </scm>
          </project>"
         (str/split-lines) (map str/trim) (str/join ""))) := true)

(defn gen-pom-properties
  [params]
  (let [{:keys [group-id artifact-id version]} params]
    (->> ["# Generated"
          (format "# %tc" (Date.))
          (format "version=%s" version)
          (format "groupId=%s" group-id)
          (format "artifactId=%s" artifact-id)]
         (str/join (System/lineSeparator)))))

(tests
 (->> (gen-pom-properties {:artifact-id "clj-sample"
                           :group-id "io.github.rynkowsg"
                           :version "1.0.0"})
      (str/split-lines)
      (drop 2))
 :=
 (->> "# Generated by org.clojure/tools.build
       # Thu Jan 11 16:11:07 ART 2024
       version=1.0.0
       groupId=io.github.rynkowsg
       artifactId=clj-sample"
       (str/split-lines) (drop 2) (map str/trim)))