# Logseq Windows ARM64 Community Build

> **This is an unofficial community fork** providing working Windows ARM64 builds.
> The official Logseq repository does not publish ARM64 releases.

## Why This Fork Exists

The official Logseq CI pipeline builds Windows ARM64 but the builds **crash on startup** due to a missing native module:

```
Error: Cannot find module '@logseq/rsapi-win32-arm64-msvc'
```

This fork removes the problematic dependencies and provides working ARM64 builds.

## What's Different

| Feature | Official Build | This Fork |
|---------|---------------|-----------|
| Core note-taking | Yes | Yes |
| Local file storage | Yes | Yes |
| Markdown/Org support | Yes | Yes |
| Whiteboards | Yes | Yes |
| Plugins | Yes | Yes |
| Database graphs | Yes | Yes |
| **Logseq Sync** | Yes | **Disabled** |
| **Git integration** | Yes | **Disabled** |

### What You Lose

- **Logseq Sync**: Cloud synchronization between devices (requires `@logseq/rsapi` which has no ARM64 build)
- **Git integration**: Auto-commit, version history (requires `dugite` which has no ARM64 Git binary)

### What You Keep

Everything else works natively on Windows ARM64:
- Create and edit pages, blocks, and journals
- Store notes locally as Markdown or Org files
- Use whiteboards for visual note-taking
- Install and use plugins
- Database-backed graphs (SQLite)

## Download

Get the latest ARM64 build from [Releases](../../releases).

**Files:**
- `Logseq-win-arm64.msi` - Windows installer (recommended)
- `*.zip` - Portable version (extract and run)
- `*.nupkg` - Squirrel update package

## Installation

### MSI Installer (Recommended)
1. Download the `.msi` file from Releases
2. Double-click to install
3. Launch Logseq from Start Menu

### Portable Version
1. Download the `.zip` file from Releases
2. Extract to any folder
3. Run `Logseq.exe`

### Opening Your Notes
When Logseq starts, click "Choose a folder" and select your existing graph folder (e.g., `OneDrive/Logseq`).

## Auto-Update Behavior

This fork uses a different update channel. The app will:
- Check for updates from this fork's releases
- **NOT** accidentally update to official x64 builds

The updater is architecture-aware and only looks for ARM64 builds.

## Building From Source

```powershell
# Clone
git clone https://github.com/johanclawson/logseq.git
cd logseq

# Install dependencies
yarn install

# Build ClojureScript
yarn gulp:build
yarn cljs:release-electron
yarn webpack-app-build

# Build Electron for ARM64
cd static
$env:npm_config_arch = "arm64"
yarn install
yarn electron:make-win-arm64

# Output: static/out/Logseq-win32-arm64/
```

## Technical Details

### Why Official ARM64 Builds Don't Work

The official CI pipeline (PR #12123) builds Windows ARM64 but:
1. The Electron shell is correctly built for ARM64
2. BUT the code still tries to `require('@logseq/rsapi')`
3. rsapi looks for `@logseq/rsapi-win32-arm64-msvc` (native Rust binary)
4. **That binary doesn't exist** - crash on startup

### Our Fix

We stub out the rsapi and dugite dependencies:
- `src/electron/electron/file_sync_rsapi.cljs` - Returns "sync disabled" errors gracefully
- `src/electron/electron/git.cljs` - Returns "git disabled" errors gracefully
- `resources/package.json` - Removes problematic native dependencies

## Upstream Sync

This fork periodically syncs with [logseq/logseq](https://github.com/logseq/logseq) to get new features and fixes, then reapplies the ARM64 patches.

## Contributing

Issues and PRs welcome! For ARM64-specific issues, please file them here. For general Logseq issues, please file them in the [official repository](https://github.com/logseq/logseq/issues).

## Credits

- [Logseq](https://github.com/logseq/logseq) - The original project
- Community contributors who helped identify the ARM64 issues

## License

Same as upstream Logseq - AGPL-3.0
