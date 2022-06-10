(ns website-downloader.downloader-new
  (:require [clojure.string :as str]
            [website-downloader.utils :as utils]
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

(defn download-website
  [^String uri ^String download-directory]
  (let [domain (web/get-website-domain uri)
        host (web/get-website-host uri)
        html (atom (web/get-website-content uri))
        resources (get-static-resources @html)
        save-path (create-directory-if-not-exists (str/join "/" (conj (web/create-path uri) domain download-directory)) "test")
        css-path (utils/add-tail (str/split save-path #"/") "css/")
        js-path (utils/add-tail (str/split save-path #"/") "js/")
        img-path (utils/add-tail (str/split save-path #"/") "img/")]

    (create-directory-if-not-exists (str/join "/" css-path) nil)
    (create-directory-if-not-exists (str/join "/" js-path) nil)
    (create-directory-if-not-exists (str/join "/" img-path) nil)

    (let [downloaded-css (map #(zipmap [:original :new] [% (future (web/download-static-file host % (str/join "/" css-path)))]) (get resources :css '()))
          downloaded-js  (map #(zipmap [:original :new] [% (future (web/download-static-file host % (str/join "/" js-path)))]) (get resources :js '()))
          downloaded-img  (map #(zipmap [:original :new] [% (future (web/download-image host % (str/join "/" img-path)))]) (get resources :img '()))
    
          css-files (map #(zipmap [:original :new] [(get % :original) (deref (get % :new))]) downloaded-css)
          js-files (map #(zipmap [:original :new] [(get % :original) (deref (get % :new))]) downloaded-js)
          img-files (map #(zipmap [:original :new] [(get % :original) (deref (get % :new))]) downloaded-img)]
      
      (doseq [css css-files] (swap! html str/replace (get css :original) (get css :new)))
      (doseq [js js-files] (swap! html str/replace (get js :original) (get js :new)))
      (doseq [img img-files] (swap! html str/replace (get img :original) (get img :new)))

      (spit (str save-path "/" "index.html") @html) (System/exit 0) )))