(ns website-downloader.core-test
  (:require [clojure.test :refer :all]
            [website-downloader.core :refer :all]
            [website-downloader.downloader :as downloader]
            [website-downloader.website :as website]
            ))

(deftest test-static-files-detection
  (testing "Test if url is a static file"
    (is (true? (downloader/static-file? "/static/test.js")))
    (is (false? (downloader/static-file? "https://")))))

(deftest test-getting-attribute-values
  (testing "Testing if function correctly gets attribute values"
    (is (= "test" (downloader/get-attribute-value "<script src='test'></script>" "src")))
    (is (not= "test" (downloader/get-attribute-value "<script src='test.js'></script>" "src")))))

(deftest test-getting-host-from-uri
  (testing "Get correct host from uri"
    (is (= "hardreset" (website/host-from-uri "https://www.hardreset.info")))
    (is (= "www.hardreset.info" (website/host-from-uri-with-protocol "https://www.hardreset.info/articles")))
    (is (= "olx" (website/host-from-uri "https://www.olx.pl")))
    )
  )