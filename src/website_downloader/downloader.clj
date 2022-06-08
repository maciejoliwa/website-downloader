(ns website-downloader.downloader
  (:require [website-downloader.website :as web]
            [website-downloader.utils :as utils]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]))

(defn- create-directory-if-not-exists
  [^String path]
  (.mkdirs (java.io.File. path)))

(defn- static-file?
  [^String src-or-href]
  (and (str/starts-with? src-or-href "/") (not (str/starts-with? src-or-href "//"))))

(defn- online-resource?
  [^String src-or-href]
  (or (str/starts-with? src-or-href "http") (str/starts-with? src-or-href "//")))

(defn download-static-resource
  [^String uri ^String path ^String file]
  (let [host (web/host-from-uri-with-protocol uri)
        filename (last (str/split file #"/"))
        
  ] (println host filename))
  )

(defn- get-attribute-value
  [^String tag ^String attribute]
  (let [pattern (re-pattern (str attribute "='(.*)'"))
        found (re-find pattern (str/replace tag #"\"" "'"))] (last found)))

(defn- download-resources
  [css-and-scripts css-path scripts-path uri]
  (let [converted-css (map #(get-attribute-value % "href") (get css-and-scripts :css))
        converted-js (map #(get-attribute-value % "src") (get css-and-scripts :scripts))
        
        ]
    
    []))

(defn download-website
  ([^String uri ^String download-directory]
   (let [hostname (web/host-from-uri uri)
         html (web/get-website-html uri)
         path (conj (web/create-path uri) hostname download-directory)
         css-and-scripts {:css (web/get-elements "link" html "link='stylesheet'")
                          :scripts (web/get-elements "script" html "src=")}
         css-path (utils/add-tail path "css/")
         scripts-path (utils/add-tail path "scripts/")]

     (create-directory-if-not-exists (str (str/join "/" path) "/"))
     (create-directory-if-not-exists (str/join "/" css-path))
     (create-directory-if-not-exists (str/join "/" scripts-path))

     (pp/pprint (download-resources css-and-scripts css-path scripts-path uri))

     (spit (str (str/join "/" path) "/" "index.html") html))
   )
  ([^String uri] (download-website uri "downloads")))