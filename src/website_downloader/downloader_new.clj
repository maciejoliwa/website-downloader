(ns website-downloader.downloader-new
  (:require [clojure.string :as str]
            [website-downloader.website-new :as web])
  )

(defn js?
  [^String f]
  (str/ends-with? (str/trim f) ".js"))

(defn css?
  [^String f]
  (str/ends-with? (str/trim f) ".css"))

(defn img?
  [^String f]
  (let [trimmed (str/trim f)]
    (or
     (str/ends-with? trimmed ".png")
     (str/ends-with? trimmed ".jpg")
     (str/ends-with? trimmed ".svg")
     (str/ends-with? trimmed ".giv")
     (str/ends-with? trimmed ".jpeg"))))

(defn static-file?
  [^String url]
  (and (not (nil? url)) (str/starts-with? url "/") (not (str/starts-with? url "//"))))

(defn get-static-resources
  [^String html]
  (let [styles (map #(web/retrieve-attribute-value % "href") (filter #(not (nil? %)) (web/get-styles html "href")))
        scripts (map #(web/retrieve-attribute-value % "src") (filter #(not (nil? %)) (web/get-scripts html "src")))
        images (map #(web/retrieve-attribute-value % "src") (filter #(not (nil? %)) (web/get-images html "src")))
        only-static-styles (filter #(and (static-file? %) (css? %)) styles)
        only-static-scripts (filter #(and (static-file? %) (js? %)) scripts)
        only-static-images (filter #(and (static-file? %) (img? %)) images)]
    (zipmap [:css :js :img] [only-static-styles only-static-scripts only-static-images])))

(defn- create-directory-if-not-exists
  "This function will create a directory if it does not exist.
   If the directory exists, program will use the alt parameter in order to 
   name the directory.

   It is prefered to pass the HTML title as the alt.
   "
  [^String path ^String alt]
  (let [directory (java.io.File. path)
        exists? (.exists directory)
        parent (.getParent directory)
        ]
    (if exists?
      (create-directory-if-not-exists (str parent "/" alt) (str alt "1"))
      (do (.mkdirs directory) path))))
