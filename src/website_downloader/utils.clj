(ns website-downloader.utils)

(defn add-tail-vec
  [s e]
  (conj (vec s) e))

(defn add-tail
  [s e]
  (apply list (add-tail-vec s e)))