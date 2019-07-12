(ns rt.parser
  (:require
   [clj-yaml.core :as yaml]
   [clojure.core.strint :refer [<<]]
   ))

(def read-file (comp yaml/parse-string slurp))

(defn in? [xs x]
  (some #(= x %) xs))

;; TODO: parse new yml format - better than the old one I think.
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

(defn fields->typescript-type-fields [xs]
  (reduce str (map (fn [_field] (<< "type ~{_field} = string\n")) xs)))

(defn map->typescript [name {:keys [extends fields immutable setters getters]}]
  (let [_type-fields (fields->typescript-type-fields fields)
        _interface-fields (fields->typescript-interface-fields fields)
        _class-fields (fields->typescript-class-fields fields setters getters)]
    (->>
     (<< "
~{_type-fields}

export interface ~{name} {
~{_interface-fields}
}

export class ~{name} {
~{_class-fields}
}
")
     (spit "/tmp/rt.ts")
     )))

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
