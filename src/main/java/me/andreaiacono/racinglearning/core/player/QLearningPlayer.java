package me.andreaiacono.racinglearning.core.player;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.core.Lap;
import me.andreaiacono.racinglearning.gui.CircuitPanel;

import java.util.Random;

public class QLearningPlayer {

    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private final int mazeWidth = 3;
    private final int mazeHeight = 3;
    private final int statesCount = mazeHeight * mazeWidth;

    private byte[] screen;
    private double[][] Q;    // Q learning

    private final Lap lap;

    public QLearningPlayer(Lap lap) {
        this.lap = lap;
    }

    public void race(Lap lap) throws Exception {

        long raceStartTime = System.currentTimeMillis();
        CircuitPanel circuit = lap.circuit;
        Car car = lap.car;

        Random rand = new Random();

        for (int i = 0; i < 1000; i++) { // Train cycles

            while (!circuit.isLapCompleted()) {

                // updates the position of the car
                car.setIsOnTrack(circuit.isCarOnTrack());
                car.updatePosition();

                // refreshes the screen with the new position
                circuit.updateCircuit(raceStartTime);

                // computes image to be sent to the RL algorithm
                byte[] currentFrame = circuit.getCurrentFrame();

                // sends data to RL algorithm
                int reward = circuit.getReward();

                // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
//                double q = Q[crtState][nextState];
//                double maxQ = maxQ(nextState);
//                int r = reward;
//
//                double value = (1-alpha) * q  + alpha * (r + gamma * maxQ);
//                Q[crtState][nextState] = value;
//
//                Directions directions = rl.getOutput(currentFrame);

                // reads commands outputted by RL algorithm
//                car.applyDirections(directions);
            }
        }
    }


}
