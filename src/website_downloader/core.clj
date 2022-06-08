(ns website-downloader.core
  (:require
   [website-downloader.downloader :as downloader])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [download-directory-location nil
        websites (filter #(or (not= % "downloads/") (not= download-directory-location %))  args)
        ]
    (if (empty? websites)
      (println "Pierwszym argumentem musi być strona internetowa!")
      (if (nil? download-directory-location)
        (doseq [website websites] (future (downloader/download-website website)))
        (doseq [website websites] (future (downloader/download-website website download-directory-location)))))))
