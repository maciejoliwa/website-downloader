(ns website-downloader.website-new
  (:require
   [clj-http.client :as client]
   [clojure.string :as str]
   [clojure.java.io :as io]
   ))

(defn get-website-content
  [^String uri]
  (let [response (client/get uri {:throw-exceptions false})]
    (if (= (get response :status) 200) (get response :body) nil)))

(defn get-website-domain
  [^String uri]
  (let [host (.getHost (java.net.URI. uri))
        www-pattern (re-pattern "www.")
        top-level-domain-pattern (re-pattern #"\.(com|info|org|app|netlify|pl|uk|co|)")]
    (-> host
        (str/replace www-pattern "")
        (str/replace top-level-domain-pattern ""))))

(defn get-website-host
  [^String uri]
  (.getHost (java.net.URI. uri)))

(defn- get-html-elements
  [^String tag ^String html filter-attribute]
  (let [tag-pattern (re-pattern (str "<" tag ">?.*"))
        found-tags (re-seq tag-pattern html)
        ]
    (cond
      (nil? filter-attribute) found-tags
      :else (->> found-tags
                 (map #(str/replace % #"\"" "'"))
                 (map str/trim)
                 (filter #(str/includes? % filter-attribute))))))

(defn get-scripts
  [^String html filters]
  (get-html-elements "script" html filters))

(defn get-styles
  [^String html filters]
  (get-html-elements "link" html filters))

(defn get-images
  [^String html filters]
  (get-html-elements "img" html filters))

(defn retrieve-attribute-value
  [^String tag ^String attr]
  (if (nil? attr) nil 
      (let [attribute-value-pattern (re-pattern (str "(?<=" attr "=)'(.*?)'"))
            retrieved-attribute-value (re-find attribute-value-pattern tag)
            ] (last retrieved-attribute-value))))

(defn download-image
  [^String host ^String uri ^String download-path]
  (let [full-uri (str "https://" host uri) filename (last (str/split uri #"/"))]
    (with-open [in (io/input-stream full-uri)
                out (io/output-stream (str download-path "/" filename))] (io/copy in out)) (str (last (str/split download-path #"/")) "/" filename) ))

(defn download-static-file
  [^String host ^String uri ^String download-path]
  (let [full-uri (str "https://" host uri)
        contents (get-website-content full-uri)
        filename (last (str/split uri #"/"))
        ] 
    (spit (str download-path filename) contents)(str (last (str/split download-path #"/")) "/" filename)))

(defn create-path
  [^String uri]
  (let [name (get-website-domain uri)
        paths (str/split name #"/")]
    (filter #(not(str/includes? % name)) paths)))