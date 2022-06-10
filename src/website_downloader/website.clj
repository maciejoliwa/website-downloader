(ns website-downloader.website
  (:require [clojure.string :as str]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            ))

(defn host-from-uri-with-protocol
  [^String uri]
  (.getHost (java.net.URI. uri)))

(defn host-from-uri
  [^String uri]
  (let [u (java.net.URI. uri)]
    (-> (.getHost u)
        (str/replace #"www." "")
        (str/replace #"\.(com|info|org|app|netlify|pl|uk|co)" ""))))

(defn get-website-html
  [^String uri]
  (get (client/get uri) :body ""))

(defn get-elements
  [^String tag-name ^String html-content & filters]
  (let [pattern (re-pattern (str "<" tag-name ">?.*"))
        found (re-seq pattern html-content)]
    (if (or (empty? filters) (nil? filters))
      found  ; We return all matches if there are no filters
      (->> found
           (map #(str/replace % #"\"" "'"))
           (filter (fn [s] (not-empty (filter #(str/includes? s %) filters))))))))

(defn create-path
  [^String uri]
  (let [without-http-or-www (str/replace (str/replace uri #"http[a-zA-Z]?:\/{2,}" "") #"www." "")
        paths (str/split without-http-or-www #"/")
        host (host-from-uri uri)
        ]
    (filter #(not(str/includes? % host)) paths)))

