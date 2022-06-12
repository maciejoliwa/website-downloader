(ns website-downloader.core-test
  (:require [clojure.test :refer :all]
            [website-downloader.core :refer :all]
            [website-downloader.downloader-new :as downloader]
            [website-downloader.website-new :as website]))

(deftest test-static-files-detection
  (testing "Test if url is a static file"
    (is (true? (downloader/static-file? "/static/test.js")))
    (is (false? (downloader/static-file? "https://")))))

(deftest test-getting-website-host
  (testing "Test if the application correctly gets hosts"
    (is (= "www.hardreset.info" (website/get-website-host "https://www.hardreset.info")))
    (is (= "www.clojure.org" (website/get-website-host "https://www.clojure.org/about/rationale")))
    (is (= "www.rust-lang.org" (website/get-website-host "https://www.rust-lang.org/tools/install")))
    (is (= "www.github.com" (website/get-website-host "https://www.github.com/maciejoliwa/website-downloader")))))

(deftest test-getting-website-domain
  (testing "Getting websites' domains"
    (is (= "hardreset" (website/get-website-domain "https://www.hardreset.info/articles")))
    (is (= "clojure" (website/get-website-domain "https://www.clojure.org")))
    (is (= "typescriptlang" (website/get-website-domain "https://www.typescriptlang.org/")))))

(deftest test-retrieving-attribute-values
  (testing "Extracting element attributes values"
    (is (= "test.js" (website/retrieve-attribute-value "<script src='test.js'></script>" "src")))
    (is (= "dir/funny.js" (website/retrieve-attribute-value "<script type='text/javascript' src='dir/funny.js'></script>" "src")))
    (is (= "style.css" (website/retrieve-attribute-value "link rel='stylesheet' href='style.css'>" "href")))))
