(ns electron.git
  "STUBBED: dugite removed for Windows ARM64 build (no Git integration)"
  (:require [electron.logger :as logger]
            [promesa.core :as p]
            [clojure.string :as string]
            ["fs-extra" :as fs]
            ["path" :as node-path]
            ["os" :as os]))

;; All Git functions stubbed - dugite native module not available on Windows ARM64

(def log-error (partial logger/error "[Git]"))

(defn- git-disabled-error []
  (js/Error. "Git integration is not available in this build (Windows ARM64)"))

(defn get-graph-git-dir
  [graph-path & {:keys [ensure-dir?]
                 :or {ensure-dir? true}}]
  (when-let [graph-path (some-> graph-path
                                (string/replace "/" "_")
                                (string/replace ":" "comma"))]
    (let [parent-dir (.join node-path (.homedir os) ".logseq" "git" graph-path)
          dir (.join node-path parent-dir ".git")]
      (when ensure-dir? (. fs ensureDirSync dir))
      dir)))

(defn run-git!
  [_graph-path _commands]
  (logger/warn "[Git] Git commands not available in this build (Windows ARM64)")
  (p/rejected (git-disabled-error)))

(defn run-git2!
  [_graph-path _commands]
  (logger/warn "[Git] Git commands not available in this build (Windows ARM64)")
  (p/rejected (git-disabled-error)))

(defn git-dir-exists?
  [graph-path]
  (try
    (let [p (.join node-path graph-path ".git")]
      (when (fs/existsSync p)
        (.isDirectory (fs/statSync p))))
    (catch :default _e
      nil)))

(defn remove-dot-git-file!
  [_graph-path]
  ;; No-op
  nil)

(defn init!
  [_graph-path]
  (logger/warn "[Git] Git init not available in this build (Windows ARM64)")
  (p/rejected (git-disabled-error)))

(defn add-all!
  [_graph-path]
  (logger/warn "[Git] Git add not available in this build (Windows ARM64)")
  (p/rejected (git-disabled-error)))

(defn commit!
  [_graph-path _message]
  (logger/warn "[Git] Git commit not available in this build (Windows ARM64)")
  (p/rejected (git-disabled-error)))

(defn add-all-and-commit-single-graph!
  [_graph-path _message]
  ;; Silently skip - don't spam logs for auto-commit
  (p/resolved nil))

(defn add-all-and-commit!
  ([]
   (add-all-and-commit! nil))
  ([_message]
   ;; Silently skip - don't spam logs for auto-commit
   (p/resolved nil)))

(defn short-status!
  [_graph-path]
  (logger/warn "[Git] Git status not available in this build (Windows ARM64)")
  (p/rejected (git-disabled-error)))

(defonce quotes-regex #"\"[^\"]+\"")
(defn wrapped-by-quotes?
  [v]
  (and (string? v) (>= (count v) 2) (= "\"" (first v) (last v))))

(defn unquote-string
  [v]
  (string/trim (subs v 1 (dec (count v)))))

(defn- split-args
  [s]
  (let [quotes (re-seq quotes-regex s)
        non-quotes (string/split s quotes-regex)
        col (if (seq quotes)
              (concat (interleave non-quotes quotes)
                      (drop (count quotes) non-quotes))
              non-quotes)]
    (->> col
         (map (fn [s]
                (if (wrapped-by-quotes? s)
                  [(unquote-string s)]
                  (string/split s #"\s"))))
         (flatten)
         (remove string/blank?))))

(defn raw!
  [_graph-path _args]
  (logger/warn "[Git] Git commands not available in this build (Windows ARM64)")
  (p/rejected (git-disabled-error)))

(defonce auto-commit-interval (atom nil))

(defn- auto-commit-tick-fn
  []
  ;; No-op - git disabled
  nil)

(defn configure-auto-commit!
  "Configure auto commit interval - DISABLED in ARM64 build"
  []
  (when @auto-commit-interval
    (swap! auto-commit-interval js/clearInterval))
  ;; Don't set up auto-commit since git is disabled
  (logger/info "[Git] Auto-commit disabled in Windows ARM64 build"))

(defn before-graph-close-hook!
  []
  ;; No-op - git disabled
  nil)
