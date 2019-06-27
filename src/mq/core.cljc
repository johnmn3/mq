(ns mq.core
  (:require [clojure.walk :as w]))

(defmacro locals []
  (into {}
    (map
      (fn [x#]
        [`'~x# x#])
      (keys #?(:clj (if (not (:ns &env))
                      &env               ;; <- in clj
                      (:locals &env))    ;; <- in cljs
               :cljs (:locals &env)))))) ;; <- for self-hosted cljs (not tested)

(defmacro magic-quote [args]
  `(let [ls# (locals)
         *gs*# (atom {})]
    (w/postwalk
       (fn [arg#]
         (if (and (seq? arg#) (= 'clojure.core/unquote (first arg#)))
           (get ls# (last arg#))
           (if (-> arg# str last (= \#))
             (let [sym# (->> arg# str drop-last (apply str) symbol)
                   gs# (-> (gensym)
                         (str "__auto__") rest (->> (apply str sym#) symbol))]
               (or (-> @*gs*# (get sym#))
                 (-> *gs*# (swap! assoc sym# gs#) (get sym#))))
             arg#)))
       (quote ~args))))

(defn mq [args]
  `(magic-quote ~args))
