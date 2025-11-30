# Logseq Windows ARM64 Community Build

> **This is an unofficial community fork** providing working Windows ARM64 builds.
> The official Logseq repository does not publish ARM64 releases.

> **Version Note:** This fork is currently at version `0.11.0`, which is ahead of the latest official stable release (`0.10.14`). This happened because we initially synced with upstream's development branch. Going forward, we will only sync with official stable releases to ensure version parity. See [Upstream Sync](#upstream-sync) for details.

## Support the Official Logseq Team

This is an **unofficial community fork**. The amazing Logseq team builds this incredible open-source tool, and they deserve your support!

**Important:** This fork disables [Logseq Sync](https://blog.logseq.com/logseq-sync-is-now-available-to-everyone/) - a paid feature that helps fund Logseq's continued development. If you find Logseq valuable, please consider supporting the official team:

**[Donate to Logseq on OpenCollective](https://opencollective.com/logseq)**

Your contribution helps them continue building this amazing knowledge management tool for everyone.

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
| **Auto-updates** | Yes | **Manual** (notification + download) |

### What You Lose

- **Logseq Sync**: Cloud synchronization between devices (requires `@logseq/rsapi` which has no ARM64 build)
- **Git integration**: Auto-commit, version history (requires `dugite` which has no ARM64 Git binary)
- **Auto-updates**: Automatic background updates via Squirrel/electron-updater (this fork shows a notification banner with a download link instead)

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

### Upgrading from Official Logseq

**Good news:** You don't need to uninstall the official x64 version first. The ARM64 installer will upgrade your existing installation in place, preserving your settings. Your notes are stored separately in your graph folder and won't be affected.

### Windows Security Warning

This build is **not code-signed** (certificates cost ~$200-400/year). Windows SmartScreen will show a warning:

> "Windows protected your PC - Microsoft Defender SmartScreen prevented an unrecognized app from starting."

**To install anyway:**
1. Click "More info"
2. Click "Run anyway"

This is normal for community builds without a certificate.

### MSI Installer (Recommended)
1. Download the `.msi` file from Releases
2. Double-click to install
3. If you see a SmartScreen warning, click "More info" → "Run anyway"
4. Launch Logseq from Start Menu

### Portable Version
1. Download the `.zip` file from Releases
2. Extract to any folder
3. Run `Logseq.exe`

### Opening Your Notes
When Logseq starts, click "Choose a folder" and select your existing graph folder (e.g., `OneDrive/Logseq`).

## Updates & Versioning

### Version Sync with Upstream

This fork **syncs versions with official Logseq releases**:
- ARM64 builds are only created when upstream Logseq releases a new version
- Release tags match upstream: when Logseq releases `0.11.1`, we release `0.11.1-arm64`
- No spam releases from weekly syncs - only actual version bumps trigger builds

### In-App Update Notifications

When a new ARM64 version is available, you'll see a notification banner in the app header:

> "Logseq 0.11.1 for ARM64 is available! [Download] [Dismiss]"

Click **Download** to open the GitHub releases page, or **Dismiss** to hide the notification.

### Update Channels

| Release Tag | Purpose |
|-------------|---------|
| `0.11.1-arm64` | Versioned release matching upstream |
| `win-arm64-latest` | Rolling release, always has the latest build |

The app checks for updates from this fork's releases only - it will **NOT** accidentally update to official x64 builds.

## Building From Source

```powershell
# Clone
git clone https://github.com/johanclawson/logseq-win-arm64.git
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

### Startup Performance Optimizations

This fork includes several optimizations for faster startup (production builds only):

**Build-time Optimizations:**

| Optimization | How It Works |
|--------------|--------------|
| **Node.js 22 Compile Cache** | Caches compiled JavaScript bytecode, avoiding re-parsing on launch |
| **Direct Function Invocation** | Uses `f(x)` instead of `f.call(null, x)` for function calls |
| **Disabled Logging** | Removes logging infrastructure from production builds |
| **No Source Maps** | Omits debugging maps in release builds |
| **Webpack Production Mode** | Enables dead code elimination and minification |

**Runtime Startup Optimizations:**

| Optimization | How It Works |
|--------------|--------------|
| **Parallel Worker + Repo Fetch** | DB worker init and repository fetch run concurrently |
| **Async Graph List Reads** | File reads for graph metadata happen in parallel |
| **Early WebGPU Check** | WebGPU capability check starts before DB restoration |
| **Deferred Git Config** | Git configuration moved to after window load |

These optimizations are applied only to release builds - development builds retain full debugging capabilities.

*Note: Uses Node.js 22's native `Module.enableCompileCache()` which supports both CommonJS and ESM modules.*

## Upstream Sync

This fork tracks **official Logseq releases** (not the development branch):

- **Weekly check**: Every Monday, GitHub Actions checks for new upstream release tags
- **Release-based sync**: Only syncs when Logseq publishes a new version (e.g., `0.10.15`)
- **Automatic PRs**: If merge is clean, a PR is created automatically
- **Conflict handling**: If conflicts occur, an issue is created with manual resolution steps

### Why Release Tags, Not Master?

The upstream `master` branch contains unreleased/experimental code. By tracking release tags:
- ARM64 users get the same stable version as official x64 users
- Version numbers match between official and ARM64 builds
- No risk of shipping broken or incomplete features

### Current Version Status

> **Note:** This fork may temporarily be ahead of or behind official releases during the sync process. Check the [Releases](../../releases) page for the current ARM64 version.

## Contributing

Issues and PRs welcome! For ARM64-specific issues, please file them here. For general Logseq issues, please file them in the [official repository](https://github.com/logseq/logseq/issues).

## Credits

- [Logseq](https://github.com/logseq/logseq) - The original project
- Community contributors who helped identify the ARM64 issues

## License

Same as upstream Logseq - AGPL-3.0
