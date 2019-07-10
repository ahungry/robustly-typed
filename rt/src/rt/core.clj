(ns rt.core
  (:require
   [rt.parser :as p]
   )
  (:gen-class))

;; Stub test to ensure we make sensible output.
(defn go []
  (-> (p/parse-file "../sample.yml")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
