(ns website-downloader.websitenew
  (:require
   [clj-http.client :as client]
   [clojure.string :as str]))

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

(defn retrieve-attribute-value
  [^String tag ^String attr]
  (if (nil? attr) nil 
      (let [attribute-value-pattern (re-pattern (str "(?<=" attr "=)'(.*?)'"))
            retrieved-attribute-value (re-find attribute-value-pattern tag)
            ] (last retrieved-attribute-value) )
      )
  )

  