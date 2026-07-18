import {readFile, unlink, writeFile} from 'node:fs/promises';
import {resolve} from 'node:path';

const [, , inputArg = '../benchmarks/benchmarks.json', outputArg = 'static/benchmarks.json'] = process.argv;
const input = resolve(process.cwd(), inputArg);
const output = resolve(process.cwd(), outputArg);
const raw = JSON.parse(await readFile(input, 'utf8'));

if (!Array.isArray(raw) || raw.length === 0) {
  throw new Error(`Expected a non-empty JMH JSON array in ${input}`);
}

const metricPaths = [
  ['primaryMetric'],
  ['secondaryMetrics', 'gc.alloc.rate'],
  ['secondaryMetrics', 'gc.alloc.rate.norm'],
  ['secondaryMetrics', 'gc.count'],
  ['secondaryMetrics', 'gc.time'],
];

const metricNames = metricPaths.map(path => path.join('.').replace('primaryMetric', 'throughput'));
const compactNumber = value => Number.isFinite(value) ? Number(value.toPrecision(8)) : null;
const readMetric = (benchmark, path) => path.reduce((value, key) => value?.[key], benchmark);
const compactMetric = metric => metric ? [
  compactNumber(metric.score),
  compactNumber(metric.scoreError),
  metric.scoreUnit,
] : null;

const first = raw[0];
const compact = {
  schemaVersion: 1,
  metadata: {
    jmhVersion: first.jmhVersion,
    jdkVersion: first.jdkVersion,
    vmName: first.vmName,
    vmVersion: first.vmVersion,
    forks: first.forks,
    warmupIterations: first.warmupIterations,
    measurementIterations: first.measurementIterations,
  },
  metrics: metricNames,
  benchmarks: raw.map(benchmark => ({
    name: benchmark.benchmark,
    dataMode: benchmark.params?.dataMode ?? '',
    values: metricPaths.map(path => compactMetric(readMetric(benchmark, path))),
  })),
};

await writeFile(output, JSON.stringify(compact));
await unlink(input);

const rawBytes = Buffer.byteLength(JSON.stringify(raw));
const compactBytes = Buffer.byteLength(JSON.stringify(compact));
const saving = ((1 - compactBytes / rawBytes) * 100).toFixed(1);
process.stdout.write(`Compacted ${raw.length} JMH results: ${rawBytes} -> ${compactBytes} bytes (${saving}% smaller)\n`);
