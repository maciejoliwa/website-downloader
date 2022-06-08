(ns website-downloader.downloader
  (:require [website-downloader.website :as web]
            [website-downloader.utils :as utils]
            [clj-http.client :as client]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn- create-directory-if-not-exists
  [^String path]
  (.mkdirs (java.io.File. path)))

(defn static-file?
  [^String src-or-href]
  (and (str/starts-with? src-or-href "/") (not (str/starts-with? src-or-href "//"))))

(defn- download-static-resource
  [^String uri ^String path ^String file ^String script-or-css]
  (let [host (web/host-from-uri-with-protocol uri)
        filename  (last (str/split (str/replace file #"('|rel='stylesheet)" "") #"/"))
        concatenated-uri (str "https://" host(str/replace file #"('|rel='stylesheet)" ""))
        save-path (str (str/join "/" path) filename)
        update-path (str/join "/" (drop (.indexOf (str/split save-path #"/") script-or-css) (str/split save-path #"/")))
        contents (client/get concatenated-uri {:throw-exceptions false})]
    (-> save-path clojure.java.io/resource clojure.java.io/file)
    (if (= (get contents :status) 200)
      (do (spit save-path (get contents :body "")) {:original (str/trim (str/replace file #"('|rel='stylesheet)" "")) :new update-path})
      { :original file :new file }
      )))

(defn get-attribute-value
  [^String tag ^String attribute]
  (let [pattern (re-pattern (str attribute "='(.*)'"))
        found (re-find pattern (str/replace tag #"\"" "'"))] (last found)))

(defn- download-resources
  [css-and-scripts css-path scripts-path uri]
  (let [converted-css (map #(get-attribute-value % "href") (get css-and-scripts :css))
        converted-js (map #(get-attribute-value % "src") (get css-and-scripts :scripts))
        static-css (map #(download-static-resource uri css-path % "css") (filter #(and (not (nil? %)) (static-file? %))  converted-css))
        static-js (map #(download-static-resource uri scripts-path % "scripts") (filter #(and (not (nil? %)) (static-file? %)) converted-js))]
    {:css static-css :js static-js}))

(defn download-website
  ([^String uri ^String download-directory]
   (let [hostname (web/host-from-uri uri)
         html (atom (web/get-website-html uri))
         path (conj (web/create-path uri) hostname download-directory)
         css-and-scripts {:css (web/get-elements "link" @html "rel=" "href=")
                          :scripts (web/get-elements "script" @html "src=")}
         css-path (utils/add-tail path "css/")
         scripts-path (utils/add-tail path "scripts/")]

     (create-directory-if-not-exists (str (str/join "/" path) "/"))
     (create-directory-if-not-exists (str/join "/" css-path))
     (create-directory-if-not-exists (str/join "/" scripts-path))

     (def resources (download-resources css-and-scripts css-path scripts-path uri))

     (doseq [js (get resources :js)] (swap! html str/replace (get js :original "") (get js :new "")))
     (doseq [css (get resources :css)] (println css) (swap! html str/replace (get css :original "") (get css :new "")))

     (spit (str (str/join "/" path) "/" "index.html") @html)))
  ([^String uri] (download-website uri "downloads")))