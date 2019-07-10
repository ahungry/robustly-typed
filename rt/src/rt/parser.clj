(ns rt.parser
  (:require
   [clj-yaml.core :as yaml]
   ))

(def read-file (comp yaml/parse-string slurp))

(defn in? [xs x]
  (some #(= x %) xs))

(def *scalars (atom [:string :int :float]))

(defn get-scalars [] @*scalars)
(defn add-scalar [s] (swap! *scalars conj (keyword s)))

(defn what [s {:keys [extends]}]
  (cond
    (in? (get-scalars) (keyword extends))
    (do (add-scalar s) :scalar)

    :else :unknown
    ))

(defn scalar->typescript [s {:keys [extends]}]
  (format "\ntype %s = %s\n" s extends))

(defn fields->typescript-interface-fields [xs]
  (reduce str (map #(str "  " % ": " % "\n") xs)))

(defn fields->typescript-class-fields [xs _ _]
  (reduce str (map #(str "    readonly " % ": " % "\n") xs)))

(defn map->typescript [s {:keys [extends fields immutable setters getters]}]
  (->
   (format "
export interface %s {
%s
}

export class %s {
%s
}
"
           s
           (fields->typescript-interface-fields fields)
           s
           (fields->typescript-class-fields fields setters getters)
           )
   prn
   ))

(defn rt->lang [s m]
  (case (what s m)
    :scalar (scalar->typescript s m)))

(defn parser [m]
  (doseq [[k v] m]
    (prn k)))

(defn go []
  (-> (read-file "../sample.yml")
      ;; parser
      ))
