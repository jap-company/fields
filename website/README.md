# Website

This website is built using [Docusaurus 2](https://docusaurus.io/), a modern static website generator.

### Installation

```bash
npm ci
```

### Local Development

```bash
npm start
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without having to restart the server.

### Build

```bash
npm run build
```

This command generates static content into the `build` directory and can be served using any static contents hosting service.

### Benchmark data

Convert raw JMH output into the compact browser format:

```bash
npm run benchmarks:compact
```

The command reads `../benchmarks/benchmarks.json` and writes `static/benchmarks.json`.

### Deployment

```bash
USE_SSH=true npm run deploy
```
