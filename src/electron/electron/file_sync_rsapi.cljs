(ns electron.file-sync-rsapi
  "STUBBED: rsapi removed for Windows ARM64 build (no sync support)"
  (:require [electron.logger :as logger]
            [promesa.core :as p]))

;; All functions stubbed - rsapi native module not available on Windows ARM64

(defn- sync-disabled-error []
  (js/Error. "Logseq Sync is not available in this build (Windows ARM64)"))

(defn- init-logger [_log-fn]
  (logger/info "rsapi: init-logger stubbed (sync disabled)"))

(defn key-gen []
  (logger/warn "rsapi: key-gen called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn set-env [_graph-uuid _env _private-key _public-key]
  (logger/warn "rsapi: set-env called but sync is disabled")
  nil)

(defn set-progress-callback [_callback]
  nil)

(defn get-local-files-meta [_graph-uuid _base-path _file-paths]
  (logger/warn "rsapi: get-local-files-meta called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn get-local-all-files-meta [_graph-uuid _base-path]
  (logger/warn "rsapi: get-local-all-files-meta called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn rename-local-file [_graph-uuid _base-path _from _to]
  (logger/warn "rsapi: rename-local-file called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn delete-local-files [_graph-uuid _base-path _file-paths]
  (logger/warn "rsapi: delete-local-files called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn fetch-remote-files [_graph-uuid _base-path _file-paths _token]
  (logger/warn "rsapi: fetch-remote-files called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn update-local-files [_graph-uuid _base-path _file-paths _token]
  (logger/warn "rsapi: update-local-files called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn download-version-files [_graph-uuid _base-path _file-paths _token]
  (logger/warn "rsapi: download-version-files called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn delete-remote-files [_graph-uuid _base-path _file-paths _txid _token]
  (logger/warn "rsapi: delete-remote-files called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn update-remote-files [_graph-uuid _base-path _file-paths _txid _token]
  (logger/warn "rsapi: update-remote-files called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn encrypt-fnames [_graph-uuid _fnames]
  (logger/warn "rsapi: encrypt-fnames called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn decrypt-fnames [_graph-uuid _fnames]
  (logger/warn "rsapi: decrypt-fnames called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn encrypt-with-passphrase [_passphrase _data]
  (logger/warn "rsapi: encrypt-with-passphrase called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn decrypt-with-passphrase [_passphrase _data]
  (logger/warn "rsapi: decrypt-with-passphrase called but sync is disabled")
  (p/rejected (sync-disabled-error)))

(defn cancel-all-requests []
  nil)

(defonce progress-notify-chan "file-sync-progress")

;; Initialize with stubbed logger
(init-logger nil)
