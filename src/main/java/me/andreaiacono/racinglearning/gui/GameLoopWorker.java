package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.misc.Directions;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;

import javax.swing.*;

public class GameLoopWorker extends SwingWorker<Void, Void> {

    // the Hertz the game is running at
    private final static int GAME_FREQUENCY = 20;

    private final static long ONE_SECOND_IN_MILLIS = 1_000;
    private final static long SCREEN_UPDATE = ONE_SECOND_IN_MILLIS / GAME_FREQUENCY;
    private final boolean manual = true;

    private Car car;
    private CircuitPanel circuitPanel;
    private DrivingKeyListener listener;

    public GameLoopWorker(Car car, CircuitPanel circuitPanel, DrivingKeyListener listener) {
        this.car = car;
        this.circuitPanel = circuitPanel;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground() throws Exception {

        long raceStartTime = System.currentTimeMillis();

        while (!circuitPanel.isLapCompleted()) {

            long startTime = System.currentTimeMillis();

            // updates the position of the car
            try {
                car.setIsOnTrack(circuitPanel.isCarOnTrack());
                car.updatePosition();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // refreshes the screen with the new position
            circuitPanel.updateCircuit(raceStartTime);

            if (!manual) {
                // sends data to RL algorithm
            }

            // checks for input
            if (manual) {
                updateCar(listener.getDirections());
                if (!circuitPanel.isCarInsideImage()) {
                    break;
                }
            }
            else {
                // reads input data from RL algorithm
            }

            // wait for the next screen update
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTimeToScreenUpdate = SCREEN_UPDATE - elapsedTime;
            Thread.sleep(remainingTimeToScreenUpdate);
        }

        if (circuitPanel.isLapCompleted()) {
            System.out.println("LAP SUCCESSFULLY COMPLETED IN " + (System.currentTimeMillis() - raceStartTime) + "ms");
        }
        else {
            System.out.println("LAP NOT COMPLETED");
        }

        System.exit(0);
        return null;
    }

    private void updateCar(Directions directions) {
        if (directions.upPressed) {
            car.accelerate(0.4);
        }
        if (directions.downPressed) {
            car.brake(0.8);
        }
        if (directions.leftPressed) {
            car.steer(-8);
        }
        if (directions.rightPressed) {
            car.steer(8);
        }
    }
}
