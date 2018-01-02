import * as React from 'react';
import { Line, ChartData } from 'react-chartjs-2';
import * as chartjs from 'chart.js';

import { ExportResult } from './App';

interface IterationsChartState {}

interface IterationsChartProps {
  result: ExportResult;
  showAvg: boolean;
  showMin: boolean;
  showValidIndividuumsRatio: boolean;
  showBest: boolean;
  iterationsMin: number;
  iterationsMax: number;
}

export class IterationsChart extends React.Component<
  IterationsChartProps,
  IterationsChartState
> {
  shouldComponentUpdate(
    nextProps: IterationsChartProps,
    nextState: IterationsChartState
  ) {
    return nextProps !== this.props;
  }

  render() {
    const {
      result,
      showBest,
      showAvg,
      showMin,
      showValidIndividuumsRatio,
      iterationsMax,
      iterationsMin
    } = this.props;

    const iterationsCount = result.score.length;
    const detail = Math.floor(iterationsCount / 1000);
    const bestValues = result.score
      .slice(iterationsMin, iterationsMax)
      .filter((val, i) => i % detail === 0);
    const avgValues = result.avgScore
      .slice(iterationsMin, iterationsMax)
      .filter((val, i) => i % detail === 0);
    const minValues = result.minScore
      .slice(iterationsMin, iterationsMax)
      .filter((val, i) => i % detail === 0);
    const validIndividuumsRatios = result.validIndividuumsRatio
      .slice(iterationsMin, iterationsMax)
      .filter((val, i) => i % detail === 0);

    const data: ChartData<any> = {
      labels: bestValues.map((val, i) => (i * detail).toString()),
      datasets: [
        {
          label: 'Best',
          data: showBest ? bestValues : [],
          steppedLine: true,
          pointRadius: 0,
          borderColor: '#29a634'
        },
        {
          label: 'Average',
          data: showAvg ? avgValues : [],
          steppedLine: true,
          pointRadius: 0,
          borderColor: '#d99e0b'
        },
        {
          label: 'Valid individuums ratio',
          data: showValidIndividuumsRatio ? validIndividuumsRatios : [],
          steppedLine: true,
          pointRadius: 0,
          borderColor: '#2965cc'
        },
        {
          label: 'Minimum',
          data: showMin ? minValues : [],
          steppedLine: true,
          pointRadius: 0,
          borderColor: '#d13913'
        }
      ]
    };

    const options: chartjs.ChartOptions = {
      animation: {
        duration: 0
      },
      legend: {
        display: false
      },
      scales: {
        yAxes: []
      }
    };

    return <Line data={data} options={options} />;
  }
}
