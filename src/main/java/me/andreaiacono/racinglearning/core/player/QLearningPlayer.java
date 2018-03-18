package me.andreaiacono.racinglearning.core.player;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.core.Command;
import me.andreaiacono.racinglearning.core.Lap;
import me.andreaiacono.racinglearning.gui.CircuitPanel;

import java.util.Random;

public class QLearningPlayer {

    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private byte[] oldFrame;

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

                // computes image to be sent to the RL algorithm
                byte[] currentFrame = circuit.getCurrentFrame();
                byte[] delta = computeDelta(currentFrame, oldFrame);
                oldFrame = currentFrame;

//                // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
//                double q = Q[crtState][nextState];
//                double maxQ = maxQ(nextState);
//                int r = reward;
//
//                double value = (1-alpha) * q  + alpha * (r + gamma * maxQ);
//                Q[crtState][nextState] = value;

                // output of the QLearning algo
                Command command = null; // getOutput();

                // apply the output to the car
                car.applyDirections(command);

                // updates the position of the car
                lap.updateCarPosition();

                // refreshes the screen with the new position
                circuit.updateCircuit(raceStartTime);

                // computes the reward of the action
                int reward = circuit.getReward();



            }
        }
    }

    /**
     * the image computed by the algorithm is the difference between the current frame
     * and the preceding frame; in this way we can give the algorithm an idea of the
     * movement of the car inside the circuit
     * @param frame1
     * @param frame2
     * @return
     */
    private byte[] computeDelta(byte[] frame1, byte[] frame2) {
        byte[] delta = new byte[frame1.length];
        for (int i=0; i< delta.length; i++) {
            delta[i] = (byte) (frame2[i] - frame1[i]);
        }
        return delta;
    }


}
