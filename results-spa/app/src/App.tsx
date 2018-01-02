import * as React from 'react';
import { Text, Checkbox, RangeSlider, Icon } from '@blueprintjs/core';
import { IterationsChart } from './IterationsChart';

import './App.css';

export interface ExportResult {
  score: Array<number>;
  avgScore: Array<number>;
  minScore: Array<number>;
  validIndividuumsRatio: Array<number>;
  bestIndividuum: Array<number>;
  solutions: { [key: string]: Array<number> };
}

interface AppState {
  showAvg: boolean;
  showMin: boolean;
  showValidIndividuumsRatio: boolean;
  showBest: boolean;
  iterationsMin: number;
  iterationsMax: number;
}

const result = require('./result.json') as ExportResult;

class App extends React.Component<{}, AppState> {
  componentWillMount() {
    this.setState({
      showAvg: true,
      showBest: true,
      showValidIndividuumsRatio: false,
      showMin: true,
      iterationsMin: 0,
      iterationsMax: 50000
    });
  }

  render() {
    const iterationsCount = result.score.length;

    return (
      <div className="App">
        <h3>Result for {iterationsCount} iterations</h3>
        <div className="divider" />
        <h4>Best permutation</h4>
        <Text>Score: {result.score[iterationsCount - 1]}</Text>
        <Text className="pt-text-muted">
          [{result.bestIndividuum.join(', ')}]
        </Text>
        <div className="divider" />
        <h4>Optimization process</h4>
        <div className="iterations-chart-options">
          <Checkbox
            checked={this.state.showMin}
            onChange={ev =>
              this.setState({ showMin: ev.currentTarget.checked })
            }
          >
            Minimum score{' '}
            <Icon
              iconName="symbol-circle"
              iconSize={Icon.SIZE_LARGE}
              className="min-line"
            />
          </Checkbox>
          <Checkbox
            checked={this.state.showAvg}
            onChange={ev =>
              this.setState({ showAvg: ev.currentTarget.checked })
            }
          >
            Average score{' '}
            <Icon
              iconName="symbol-circle"
              iconSize={Icon.SIZE_LARGE}
              className="avg-line"
            />
          </Checkbox>
          <Checkbox
            checked={this.state.showValidIndividuumsRatio}
            onChange={ev =>
              this.setState({
                showValidIndividuumsRatio: ev.currentTarget.checked
              })
            }
          >
            Valid individuums ratio{' '}
            <Icon
              iconName="symbol-circle"
              iconSize={Icon.SIZE_LARGE}
              className="individuums-ratio-line"
            />
          </Checkbox>
          <Checkbox
            checked={this.state.showBest}
            onChange={ev =>
              this.setState({ showBest: ev.currentTarget.checked })
            }
          >
            <span>
              Best score{' '}
              <Icon
                iconName="symbol-circle"
                iconSize={Icon.SIZE_LARGE}
                className="best-line"
              />
            </span>
          </Checkbox>
        </div>
        <div className="iterations-slider">
          <Text>Iterations: </Text>
          <RangeSlider
            className="pt-fill"
            min={0}
            max={iterationsCount}
            onRelease={val =>
              this.setState({
                iterationsMin: val[0],
                iterationsMax: val[1]
              })
            }
            value={[this.state.iterationsMin, this.state.iterationsMax]}
          />
        </div>

        <IterationsChart {...this.state} result={result} />
      </div>
    );
  }
}

export default App;
