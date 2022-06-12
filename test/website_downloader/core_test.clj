(ns website-downloader.core-test
  (:require [clojure.test :refer :all]
            [website-downloader.core :refer :all]
            [website-downloader.downloader-new :as downloader]
            [website-downloader.website-new :as website]
            ))

(deftest test-static-files-detection
  (testing "Test if url is a static file"
    (is (true? (downloader/static-file? "/static/test.js")))
    (is (false? (downloader/static-file? "https://")))))