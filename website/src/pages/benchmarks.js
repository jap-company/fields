import React, {useEffect, useState} from 'react';
import {CartesianGrid, LabelList, Legend, Line, LineChart, Tooltip, XAxis, YAxis} from 'recharts';
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";
import useBaseUrl from '@docusaurus/useBaseUrl';
import Layout from "@theme/Layout";

const barColors = [
  "#ff6384", "#36a2eb", "#cc65fe", "#ffce56", "#2ecc71",
  "#e74c3c", "#3498db", "#9b59b6", "#f39c12", "#1abc9c",
  "#e67e22", "#34495e", "#95a5a6", "#f1c40f", "#2c3e50",
  "#d35400", "#16a085", "#2980b9", "#c0392b", "#8e44ad",
  "#27ae60", "#7f8c8d", "#f5b041", "#6c3483", "#117a65",
  "#5dade2", "#e74c3c", "#f0e68c", "#d2b48c", "#48c9b0",
  "#ff1493", "#7d3c98", "#2980b9", "#f8c471", "#c70039",
  "#900c3f", "#ff5733", "#c0e218", "#4a235a", "#00a8ff",
  "#d4ac0d", "#3d3d6b", "#e056fd", "#2e86de", "#f7b731",
  "#8854d0", "#ff6347", "#fed330", "#a29bfe", "#6d214f"
];

const DATA_MODES = ["all_valid", "all_invalid", "random"]
const DATA_MODE_LABEL = {
  "all_valid": "Valid",
  "all_invalid": "Invalid",
  "random": "Random"
}

const CommonLineChartByDataMode = ({title, data}) => {
  const [width, height] = useSize();
  const [activeLine, setActiveLine] = useState(null);

  // Group data by benchmark name
  const lines = DATA_MODES.map(mode => {
    return Object.fromEntries(
      data
        .filter(x => x.dataMode === mode)
        .filter(x => x.validated !== "FailFast")
        .map(x => [x.name, {
          score: x.score,
          scoreError: x.scoreError,
          scoreUnit: x.scoreUnit,
          library: x.library,
          abstraction: x.abstraction,
          effect: x.effect,
          validated: x.validated,
        }])
        .concat([["dataMode", mode]])
    )
  });

  return (
    <div>
      <h1 style={{textAlign: "center"}}>{title}</h1>
      <LineChart width={width} height={800} margin={{top: 20, right: 30, left: 40, bottom: 20}} data={lines}>
        <CartesianGrid strokeDasharray="3 3"/>
        <XAxis padding={{left: 60, right: 60}} dataKey="dataMode" tickFormatter={value => DATA_MODE_LABEL[value]}
               type="category" tick={{fontSize: 14}}/>
        <YAxis padding={{top: 60}} domain={[0, 'auto']}/>
        {activeLine == null && <Tooltip content={<BenchmarkTooltip/>}/>}
        <Legend/>
        {[lines[0]].flatMap(line => (
          Object.entries(line).filter(x => x[0] !== "dataMode").map((benchmark, index) => {
              const benchmarkName = benchmark[0];
              const dataKey = benchmarkName + ".score";
              const strokeColor = barColors[index % barColors.length];

              const noActiveLine = activeLine == null
              const isActiveLine = activeLine === benchmarkName;

              return (<Line
                isAnimationActive={false}
                type="monotone"
                key={benchmarkName}
                name={benchmarkName}
                dataKey={dataKey}
                stroke={strokeColor}
                strokeOpacity={noActiveLine || isActiveLine ? 1 : 0.1}
                focusable
                strokeWidth={isActiveLine ? 9 : 3}
                activeDot={{r: 6}}
                dot={{r: 3}}
                onMouseEnter={() => setActiveLine(benchmarkName)}
                onMouseLeave={() => setActiveLine(null)}
              >
                {isActiveLine && (
                  <LabelList
                    dataKey={dataKey}
                    position="top"
                    formatter={(value) => value.toLocaleString() + ' ops/s'}
                    style={{fill: strokeColor, fontWeight: 600}}
                    content={({x, y, value}) => (
                      <text
                        x={x}
                        y={y - 30} // increase this value for more spacing
                        fill={strokeColor}
                        opacity={0.8}
                        fontSize={20}
                        fontWeight="700"
                        stroke={strokeColor}
                        textAnchor="middle"
                      >
                        {value.toLocaleString() + ' ops/s'}
                      </text>
                    )}
                  />
                )}
              </Line>)
            }
          )
        ))}
      </LineChart>
    </div>
  );
};

const libraries = ["fields", "octopus", "dupin"]
const abstractions = ["Value", "Lens", "Schema", "Validator"]
const effects = ["Sync", "Zio", "CatsIo", "CatsEval"]
const validateds = ["Accumulate", "FailFast", "Chain", "ValidatedNec"]

const expandBenchmarkData = (payload) => {
  if (Array.isArray(payload)) return payload;
  if (payload?.schemaVersion !== 1) throw new Error(`Unsupported benchmark data schema: ${payload?.schemaVersion}`);

  return payload.benchmarks.map(benchmark => {
    const metrics = Object.fromEntries(payload.metrics.map((name, index) => [name, benchmark.values[index]]));
    const expandMetric = metric => metric && ({score: metric[0], scoreError: metric[1], scoreUnit: metric[2]});

    return {
      benchmark: benchmark.name,
      params: {dataMode: benchmark.dataMode},
      primaryMetric: expandMetric(metrics.throughput),
      secondaryMetrics: {
        "gc.alloc.rate": expandMetric(metrics["secondaryMetrics.gc.alloc.rate"]),
        "gc.alloc.rate.norm": expandMetric(metrics["secondaryMetrics.gc.alloc.rate.norm"]),
        "gc.count": expandMetric(metrics["secondaryMetrics.gc.count"]),
        "gc.time": expandMetric(metrics["secondaryMetrics.gc.time"]),
      },
    };
  });
};

const BenchmarkTooltip = ({active, payload, label}) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload

    return (
      <div style={{backgroundColor: 'white', padding: '16px', borderRadius: '16px'}}>
        <h2 style={{textAlign: "center"}}>{DATA_MODE_LABEL[label]}</h2>
        <table>
          <thead>
          <tr>
            <th>Library</th>
            <th>Abstraction</th>
            <th>Effect</th>
            <th>Validated</th>
            <th>Score</th>
            <th>Score Error</th>
            <th>Score Unit</th>
          </tr>
          </thead>
          <tbody>
          {Object.entries(data)
            .sort((a, b) => b[1].score - a[1].score)
            .filter(x => x[0] !== "dataMode")
            .map(([name, data]) =>
              <tr key={name}>
                <td>{data.library.toString()}</td>
                <td>{data.abstraction.toString()}</td>
                <td>{data.effect.toString()}</td>
                <td>{data.validated.toString()}</td>
                <td>{data.score.toString()}</td>
                <td>{data.scoreError.toString()}</td>
                <td>{data.scoreUnit.toString()}</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    );
  }

  return null;
};

export default function BenchmarkVisualizer() {
  const {siteConfig} = useDocusaurusContext();
  const [dataByMetric, setDataByMetric] = useState({});
  const benchmarksUrl = useBaseUrl("benchmarks.json")

  useEffect(() => {
    fetch(benchmarksUrl)
      .then((response) => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then((benchmarkJson) => {
        const benchmarks = expandBenchmarkData(benchmarkJson).map((b) => {
          const benchmarkName = b.benchmark.split('.').slice(2).join('.');
          const library = libraries.find(x => benchmarkName.includes(x));
          const abstraction = abstractions.find(x => benchmarkName.includes(x));
          const effect = effects.find(x => benchmarkName.includes(x));
          const validated = validateds.find(x => benchmarkName.includes(x));

          return ({
            ...b,
            name: `${library} ${abstraction} ${effect} ${validated}`,
            library, abstraction, effect, validated,
            dataMode: b.params?.dataMode || ""
          });
        });

        const metrics = [
          {key: ['primaryMetric'], title: 'Throughput', fillColor: '#8884d8'},
          {key: ['secondaryMetrics', 'gc.alloc.rate'], title: 'GC Allocation Rate', fillColor: '#82ca9d'},
          {
            key: ['secondaryMetrics', 'gc.alloc.rate.norm'],
            title: 'GC Allocation Rate Norm',
            fillColor: '#ffc658'
          },
          {key: ['secondaryMetrics', 'gc.count'], title: 'GC Count', fillColor: '#8dd1e1'},
          {key: ['secondaryMetrics', 'gc.time'], title: 'GC Time', fillColor: '#d0ed57'},
        ];

        const dataByMetric = {};

        metrics.forEach((metric) => {
          benchmarks.forEach((benchmark) => {
            let metricData = benchmark;

            metric.key.forEach((part) => metricData = metricData ? metricData[part] : null);

            if (metricData !== null && metricData !== undefined) {
              const chartKey = metric.title;
              if (!dataByMetric[chartKey]) {
                dataByMetric[chartKey] = [];
              }
              dataByMetric[chartKey].push({
                ...benchmark,
                score: Math.floor(metricData.score),
                scoreError: Math.floor(metricData.scoreError),
                scoreUnit: metricData.scoreUnit,
                unit: metric.unit || '',
              });
            }
          });
        });

        setDataByMetric(dataByMetric);
      })
      .catch((error) => {
        console.error('There was a problem with the fetch operation:', error);
      });
  }, []);

  return (
    <Layout
      title={siteConfig.tagline}
      description={`${siteConfig.title} - ${siteConfig.tagline}`}
    >
      {Object.keys(dataByMetric).map((chartKey) => (
        <CommonLineChartByDataMode
          key={chartKey}
          title={chartKey}
          data={dataByMetric[chartKey]}
        />
      ))}
    </Layout>
  );
}


const useSize = () => {
  const [windowSize, setWindowSize] = useState([
    typeof window === 'undefined' ? 1200 : window.innerWidth,
    typeof window === 'undefined' ? 800 : window.innerHeight,
  ]);

  useEffect(() => {
    const windowSizeHandler = () => {
      setWindowSize([window.innerWidth, window.innerHeight]);
    };
    window.addEventListener("resize", windowSizeHandler);

    return () => {
      window.removeEventListener("resize", windowSizeHandler);
    };
  }, []);

  return windowSize;
};
