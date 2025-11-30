# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Logseq is a privacy-first, open-source knowledge management and collaboration platform. It supports both file-based graphs (Markdown/Org-mode) and database graphs (SQLite-backed). The codebase is primarily written in ClojureScript, with React/Rum for UI.

## Build Commands

**Browser Development:**
```bash
yarn install
yarn watch              # Dev server at http://localhost:3001
```

**Desktop App Development:**
```bash
yarn install && cd static && yarn install && cd ..
yarn watch              # Wait for "Build Completed" for :electron and :app
yarn dev-electron-app   # In a separate terminal
# Or use: bb dev:electron-start
```

**Production Builds:**
```bash
yarn release            # Browser - outputs to static/
yarn release-electron   # Desktop - outputs to static/out/
```

## Testing Commands

**Linting and Unit Tests:**
```bash
bb dev:lint-and-test    # Run all linters and unit tests
bb lint:dev             # Run all linters only
yarn test               # Run unit tests only
```

**Focus Testing (run specific tests):**
1. Add `^:focus` metadata to test: `(deftest ^:focus test-name ...)`
2. Run: `bb dev:test -i focus`

**E2E Tests:**
```bash
bb dev:e2e-basic-test       # Basic e2e tests
bb dev:e2e-rtc-extra-test   # RTC (Real Time Collaboration) tests
```

**Individual Linters:**
```bash
bb lint:kondo-git-changes   # Fast lint for changed files only
bb lint:carve               # Detect unused vars
bb lint:large-vars          # Check for overly complex functions
bb lint:ns-docstrings       # Check namespace documentation
```

## Architecture

**Tech Stack:**
- ClojureScript compiled via Shadow-cljs
- React wrapped with Rum for UI components
- DataScript for in-memory database with Datalog queries
- Electron for desktop app

**Source Organization:**
- `src/main/frontend/` - Main frontend code
  - `components/` - UI components (Rum/React)
  - `handler/` - System handlers and business logic
  - `worker/` - Web worker code (separate asset)
  - `common/` - Shared code between worker and frontend
  - `worker/rtc/` - Real Time Collaboration code
- `src/electron/` - Electron desktop app specific code
- `src/test/` - Unit tests
- `deps/` - Internal ClojureScript libraries
  - `graph-parser/` - Parses Logseq graphs
  - `db/` - Database operations
  - `outliner/` - Outliner operations
- `packages/` - JavaScript dependencies
  - `ui/` - shadcn-based component system
  - `tldraw/` - Custom fork for whiteboards
- `clj-e2e/` - End-to-end Clojure tests

**State Management:**
- Document state (pages, blocks): stored in DataScript
- UI state: stored in Clojure atoms
- Components subscribe via Rum reactive components

**Code Conventions:**
- Keywords defined using `logseq.common.defkeywords/defkeyword`
- File/DB-specific code goes in `file_based/` and `db_based/` directories
- Worker and frontend namespaces must stay separate

## REPL Setup

**VSCode + Calva:**
1. Run `yarn watch` (starts nREPL on port 8701)
2. `Cmd+Shift+P` → "Calva: Connect to a Running REPL Server in the Project"
3. Select: logseq → shadow-cljs → :app → localhost:8701

**Web Worker REPL:**
Use `(shadow.user/worker-repl)` or check http://localhost:9630/runtimes for runtime IDs.

## Database Graph Tasks

```bash
bb dev:validate-db GRAPH_NAME      # Validate DB graph schema
bb dev:db-query GRAPH '[:find ...]' # Query a DB graph
bb dev:db-import FILE_GRAPH DB_NAME # Import file graph to DB
```

## Windows Development

Install dependencies via scoop:
```
scoop bucket add scoop-clojure https://github.com/littleli/scoop-clojure
scoop bucket add extras
scoop bucket add java
scoop install java/openjdk clj-deps babashka leiningen nodejs-lts
```

Or via winget + clj-msi installer from https://github.com/casselc/clj-msi/releases/

---

## ARM64 Fork Information

> **This is a community fork** providing working Windows ARM64 builds.
> The sections below are specific to this fork.

### Why This Fork Exists

The official Logseq CI builds ARM64 but crashes on startup:
```
Error: Cannot find module '@logseq/rsapi-win32-arm64-msvc'
```

The `@logseq/rsapi` package (Rust native module for sync/encryption) doesn't publish a `win32-arm64` binary. This fork stubs out rsapi and dugite to make the app work.

### Disabled Features

| Feature | Reason |
|---------|--------|
| Logseq Sync | Requires `@logseq/rsapi` (no ARM64 build) |
| Git integration | Requires `dugite` (no ARM64 Git binary) |

### Stubbed Files (DO NOT REVERT)

These files contain ARM64 compatibility stubs. When syncing with upstream, **preserve our modifications**:

| File | What's Stubbed |
|------|----------------|
| `src/electron/electron/file_sync_rsapi.cljs` | All rsapi functions return rejected promises |
| `src/electron/electron/git.cljs` | All git/dugite functions return rejected promises |
| `src/electron/electron/utils.cljs` | Removed rsapi import, stubbed `set-rsapi-proxy` |
| `resources/package.json` | Removed `@logseq/rsapi` and `dugite` dependencies |
| `resources/electron-entry.js` | Entry point that enables Node.js 22 compile cache before main |
| `src/electron/electron/updater.cljs` | ARM64 update checker (checks this fork's releases) |
| `src/main/frontend/components/header.cljs` | ARM64 update notification banner |

### Performance Optimizations

This fork includes startup optimizations (production builds only):

**Build-time Optimizations:**

| Optimization | File | Impact |
|--------------|------|--------|
| Node.js 22 Compile Cache | `resources/electron-entry.js` | 30-50% faster startup |
| Direct Function Invocation | `shadow-cljs.edn` (`:fn-invoke-direct`) | 10-30% faster |
| Disabled Logging | `shadow-cljs.edn` (`goog.debug.LOGGING_ENABLED`) | ~5-10% faster |
| No Source Maps | `shadow-cljs.edn` (`:source-map false`) | Smaller bundles |
| Webpack Production Mode | `webpack.config.js` | Tree shaking enabled |

Note: Uses Node.js 22's native `Module.enableCompileCache()` instead of `v8-compile-cache` package (which doesn't support ESM modules).

**Runtime Startup Optimizations:**

| Optimization | File |
|--------------|------|
| Parallelize worker + repo fetch | `src/main/frontend/handler.cljs` |
| Async file reads for graph list | `src/electron/electron/handler.cljs` |
| Move WebGPU check earlier | `src/main/frontend/handler.cljs` |
| Defer git config | `src/electron/electron/core.cljs` |

These changes are safe because:
- Worker init and repo fetch are independent operations (no state conflicts)
- Async file reads use proper error handling for missing files
- Git config is already stubbed for ARM64 (returns rejected promise)

### In-App Update Notifications

ARM64 users get update notifications via:
1. `src/electron/electron/updater.cljs` - Checks GitHub API for new releases
2. `src/electron/electron/core.cljs` - Sends update info to renderer on startup
3. `src/main/frontend/components/header.cljs` - Displays notification banner

### ARM64 Build Commands

```powershell
# Full build (ClojureScript + Electron ARM64)
yarn install
yarn gulp:build
yarn cljs:release-electron
yarn webpack-app-build

cd static
$env:npm_config_arch = "arm64"
yarn install
yarn electron:make-win-arm64

# Output: static/out/make/
```

**Requirements:**
- Node.js 22.20.0+ (not 20!)
- Java 21 (Temurin)
- Clojure CLI 1.11+

### GitHub Actions Workflows

| Workflow | File | Trigger | Purpose |
|----------|------|---------|---------|
| Build ARM64 | `.github/workflows/build-win-arm64.yml` | `version.cljs` changes or manual | Build & publish ARM64 releases |
| Sync Upstream | `.github/workflows/sync-upstream.yml` | Weekly (Mon 6:00 UTC) | Sync with upstream release tags |

**Build workflow jobs:**
1. `compile-cljs` (Ubuntu) - Builds ClojureScript with optimizations
2. `build-windows-arm64` (Windows) - Builds Electron with `npm_config_arch=arm64`
3. `release` (Ubuntu) - Publishes versioned (`0.11.0-arm64`) and rolling (`win-arm64-latest`) releases

**Sync workflow behavior:**
- Tracks **release tags** (not master) - only syncs when upstream publishes a new version
- Compares `version.cljs` with latest upstream tag (e.g., `0.10.14`, `0.11.0`)
- Creates PR if merge is clean, issue if conflicts occur
- Can be triggered manually via workflow_dispatch

### Upstream Sync Process

Automatic (weekly):
- Checks for new upstream release tags
- Only syncs when a newer version exists (won't sync if fork is ahead)

Manual:
```bash
git remote add upstream https://github.com/logseq/logseq.git
git fetch upstream --tags
git merge <release-tag>  # e.g., 0.10.15
# Resolve conflicts - KEEP OUR STUBS in the files listed above
git push origin master
```

### Conflict Resolution

When upstream modifies stubbed files:
1. Keep our stub implementations (rejected promises)
2. Add any NEW functions from upstream as additional stubs
3. Test build locally before pushing
4. Check that rsapi/dugite are NOT re-added to package.json

### Release Process

Releases are automatic on push to master:
- Tag: `win-arm64-latest` (rolling release)
- Artifacts: MSI, ZIP, nupkg
- URL: https://github.com/johanclawson/logseq-win-arm64/releases/tag/win-arm64-latest

### Links

- **Fork**: https://github.com/johanclawson/logseq-win-arm64
- **Upstream**: https://github.com/logseq/logseq
- **CI Status**: https://github.com/johanclawson/logseq-win-arm64/actions
- **Releases**: https://github.com/johanclawson/logseq-win-arm64/releases
