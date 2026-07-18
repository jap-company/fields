# Benchmarks

Fields uses JMH to compare the Value, Lens, and Schema front ends with multiple validation containers and selected external libraries.

Run the full suite from the repository root:

```bash
./benchmarks.sh
```

The command writes full JMH data to `benchmarks/benchmarks.json`, then runs `npm --prefix website run benchmarks:compact`. The preprocessing step keeps only chart metrics and shared environment metadata, rounds values to useful display precision, and writes minified data to `website/static/benchmarks.json`.

This separation keeps raw local results available for analysis while avoiding large percentile arrays, raw iteration samples, repeated JVM arguments, and repeated environment metadata in the published site.

For comparable results, record the commit, CPU/OS, JDK, Scala version, forks, warmup, measurement settings, and data mode. Do not treat small differences from different machines as regressions.

