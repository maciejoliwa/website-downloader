(ns website-downloader.core
  (:require
   [website-downloader.downloader-new :as downloader])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [download-directory-location nil
        websites (filter #(or (not= % "downloads/") (not= download-directory-location %))  args)
        ]
    (if (empty? websites)
      (println "Nie podano żadnych stron do pobrania!")
      (if (nil? download-directory-location)
        (doseq [website websites] (downloader/download-website website "downloads"))
        (doseq [website websites] (downloader/download-website website "downloads"))))))
