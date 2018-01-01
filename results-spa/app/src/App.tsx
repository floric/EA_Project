import * as React from 'react';
import { Line } from 'react-chartjs-2';
import './App.css';

interface ExportResult {
  score: Array<Number>;
  avgScore: Array<Number>;
  minScore: Array<Number>;
  solutions: { [key: string]: Array<Number> };
}

const result = require('./result.json') as ExportResult;
const displayEnd = 10000;

class App extends React.Component {
  render() {
    const bestValues = result.score.slice(0, displayEnd);
    const avgValues = result.avgScore.slice(0, displayEnd);
    const minima = result.minScore.slice(0, displayEnd);

    return (
      <div className="App">
        <Line
          data={{
            labels: bestValues.map((val, i) => (i % 100 !== 0 ? '' : i)),
            datasets: [
              {
                label: 'Best',
                data: bestValues,
                steppedLine: true,
                pointRadius: 0
              },
              {
                label: 'Average',
                data: avgValues,
                steppedLine: true,
                pointRadius: 0,
                borderColor: 'blue'
              },

              {
                label: 'Minimum',
                data: minima,
                steppedLine: true,
                pointRadius: 0,
                borderColor: 'red'
              }
            ]
          }}
          options={{
            legend: {
              display: false
            },
            elements: {
              line: {
                cubicInterpolationMode: 'monotone'
              }
            },
            scales: {
              yAxes: []
            }
          }}
        />
      </div>
    );
  }
}

export default App;
