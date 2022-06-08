(ns website-downloader.core-test
  (:require [clojure.test :refer :all]
            [website-downloader.core :refer :all]
            [website-downloader.downloader :as downloader]
            ))

(deftest test-static-files-detection
  (testing "Test if url is a static file"
    (is (true? (downloader/static-file? "/static/test.js")))
    (is (false? (downloader/static-file? "https://")))
    )
  )

(deftest test-getting-attribute-values
  (testing "Testing if function correctly gets attribute values"
    (is (= "test" (downloader/get-attribute-value "<script src='test'></script>" "src")))
    (is (not= "test" (downloader/get-attribute-value "<script src='test.js'></script>" "src")))
    )
  )