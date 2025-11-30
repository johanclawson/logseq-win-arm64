// V8 compile cache - must be loaded FIRST before any other modules
// This caches compiled JavaScript bytecode to avoid re-parsing on subsequent launches
// Impact: 30-50% faster startup time
require('v8-compile-cache');

// Load the main Electron application
require('./electron.js');
