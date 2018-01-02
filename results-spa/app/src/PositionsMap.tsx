import * as React from 'react';
import { Scatter, ChartData } from 'react-chartjs-2';

import { ExportResult } from './App';
import { Text } from '@blueprintjs/core';

interface PositionsMapState {
  time: number;
}

interface PositionsMapProps {
  result: ExportResult;
}

interface Pos {
  x: number;
  y: number;
}

const lerp = (a: Pos, b: Pos, val: number): Pos => {
  return {
    x: a.x * (1 - val) + b.x * val,
    y: a.y * (1 - val) + b.y * val
  };
};

const distanceBetween = (a: Pos, b: Pos) => {
  return Math.sqrt(Math.pow(a.x - b.x, 2.0) + Math.pow(a.y - b.y, 2.0));
};

export class PositionsMap extends React.Component<
  PositionsMapProps,
  PositionsMapState
> {
  componentWillMount() {
    this.setState({
      time: 0.0
    });
    setInterval(() => {
      this.increaseTime();
    }, 150);
  }

  increaseTime = () => {
    const newTime = this.state.time + 0.01;
    if (newTime > 1.0) {
      this.setState({
        time: 0
      });
    } else {
      this.setState({
        time: newTime
      });
    }
  };

  render() {
    const { result, result: { bestIndividuum } } = this.props;

    const homePos = Object.keys(result.positions)
      .map(key => result.positions[key] || [])
      .map(obj => ({ x: obj[0], y: obj[1] }));

    const positionChanges: Map<number, Array<Pos>> = new Map();

    // start from home
    for (let teamIndex = 0; teamIndex < homePos.length; teamIndex++) {
      const teamWay = [];
      const teamPos = homePos[teamIndex];
      teamWay.push(teamPos);
      teamWay.push(teamPos); // add twice for short break

      positionChanges.set(teamIndex, teamWay);
    }

    // go through meals
    for (
      let mealIndex = 0;
      mealIndex < bestIndividuum.length / 3;
      mealIndex++
    ) {
      const meal = bestIndividuum.slice(mealIndex * 3, mealIndex * 3 + 3);
      const cookIndex = meal[0];
      const cookPos = homePos[cookIndex];
      const guestIndices = meal.slice(1);
      guestIndices.forEach(teamIndex => {
        const teamWay = positionChanges.get(teamIndex) || [];
        teamWay.push(cookPos);
        positionChanges.set(teamIndex, teamWay);
      });

      const cookWay = positionChanges.get(cookIndex) || [];
      cookWay.push(cookPos);
      positionChanges.set(cookIndex, cookWay);
    }

    // go back home
    for (let teamIndex = 0; teamIndex < homePos.length; teamIndex++) {
      const teamWay = positionChanges.get(teamIndex) || [];
      const teamPos = homePos[teamIndex];
      teamWay.push(teamPos);
      positionChanges.set(teamIndex, teamWay);
    }

    // 5.0, because 5 values per way
    const mealTransitionIndex = Math.floor(this.state.time * 5.0);
    const mealTransitionTime = this.state.time * 5.0 - mealTransitionIndex;

    const changePosData = Array.from(positionChanges.keys()).map(teamIndex =>
      lerp(
        positionChanges.get(teamIndex)![mealTransitionIndex],
        positionChanges.get(teamIndex)![mealTransitionIndex + 1],
        mealTransitionTime
      )
    );

    const distances = Array.from(positionChanges.keys())
      .map(teamIndex =>
        distanceBetween(
          positionChanges.get(teamIndex)![mealTransitionIndex],
          positionChanges.get(teamIndex)![mealTransitionIndex + 1]
        )
      )
      .reduce((a, b) => a + b);

    const data: ChartData<any> = {
      labels: Object.keys(result.positions),
      datasets: [
        {
          label: 'Homes',
          data: homePos,
          borderColor: '#d99e0b'
        },
        {
          label: 'Teams',
          data: changePosData,
          borderColor: '#2965cc'
        }
      ]
    };

    const options = {
      animation: {
        duration: 0
      },
      legend: {
        display: true
      },
      scales: {
        yAxes: [
          {
            type: 'linear',
            ticks: {
              autoSkip: false,
              min: 0.0,
              max: 1.0
            }
          }
        ],
        xAxes: [
          {
            type: 'linear',
            ticks: {
              autoSkip: false,
              min: 0.0,
              max: 1.0
            }
          }
        ]
      }
    };

    return (
      <div>
        <Text>
          <strong>Current Meal:</strong>{' '}
          {mealTransitionIndex === 0
            ? 'Home'
              : mealTransitionIndex === 1
              ? 'Home to Starter'
              : mealTransitionIndex === 2
                ? 'Start to Main'
                : mealTransitionIndex === 3
                  ? 'Main to Dessert'
                  : 'Dessert to Home'}{' '}
          | <strong>Distance:</strong> {distances}
        </Text>
        <Scatter data={data} options={options} />
      </div>
    );
  }
}
