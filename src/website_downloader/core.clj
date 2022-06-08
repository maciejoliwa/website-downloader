(ns website-downloader.core
  (:require
   [website-downloader.downloader :as downloader])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [website (first args)
        download-directory-location (second args)]
    (if (nil? website)
      (println "Pierwszym argumentem musi być strona internetowa!")
      (if (nil? download-directory-location)
        (downloader/download-website website) (downloader/download-website website download-directory-location)))))