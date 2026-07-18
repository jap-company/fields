sbt "benchmarks/Jmh/run -jvmArgsAppend \"-Djmh.executor=FJP\" -t 8 -prof gc -rf json -rff \"$PWD/benchmarks/benchmarks.json\" .*"
npm --prefix website run benchmarks:compact