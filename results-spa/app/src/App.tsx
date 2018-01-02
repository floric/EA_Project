import * as React from 'react';
import {
  Text,
  Checkbox,
  Icon,
  NumericInput,
  ControlGroup
} from '@blueprintjs/core';
import { IterationsChart } from './IterationsChart';

import './App.css';
import { PositionsMap } from './PositionsMap';

export interface ExportResult {
  individualsCount: number;
  score: Array<number>;
  avgScore: Array<number>;
  minScore: Array<number>;
  validIndividuumsRatio: Array<number>;
  bestIndividuum: Array<number>;
  solutions: { [key: string]: Array<number> };
  positions: { [key: string]: Array<number> };
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
      iterationsMax: result.score.length === 0 ? 1 : result.score.length
    });
  }

  render() {
    const iterationsCount = result.score.length;

    return (
      <div className="App">
        <h3>Solution after {iterationsCount} iterations</h3>
        <div className="divider" />
        <h4>Best permutation</h4>
        <Text><strong>Score:</strong> {result.score[iterationsCount - 1]} | <strong>Individuals:</strong> {result.individualsCount}</Text>
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
          <div className="iterations-borders">
            <Text>Iterations:</Text>
            <ControlGroup fill={true}>
              <NumericInput
                className="pt-fixed"
                placeholder="Minimum iteration"
                min={0}
                max={this.state.iterationsMax}
                value={this.state.iterationsMin}
                onValueChange={val =>
                  this.setState({
                    iterationsMin: val
                  })
                }
              />
              <NumericInput
                className="pt-fixed"
                placeholder="Maximum iteration"
                min={this.state.iterationsMin}
                max={result.score.length === 0 ? this.state.iterationsMin + 1 : result.score.length}
                onValueChange={val =>
                  this.setState({
                    iterationsMax: val
                  })
                }
                value={this.state.iterationsMax}
              />
            </ControlGroup>
          </div>
        </div>
        <div className="chart-container">
          <div className="chart">
            <IterationsChart {...this.state} result={result} />
          </div>
        </div>
        <div className="divider" />
        <h3>Positions</h3>
        <div className="chart-container">
          <div className="chart">
            <PositionsMap result={result} />
          </div>
        </div>
      </div>
    );
  }
}

export default App;
