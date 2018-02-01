package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;

import javax.swing.*;

public class GameLoopWorker extends SwingWorker<Void, Void> {

    // the Hertz the game is running at
    private final static int GAME_FREQUENCY = 25;

    private final static long ONE_SECOND_IN_MILLIS = 1_000;
    private final static long SCREEN_UPDATE = ONE_SECOND_IN_MILLIS / GAME_FREQUENCY;

    protected Car car;
    private CircuitPanel circuitPanel;
    boolean isRunning = true;

    public GameLoopWorker(Car car, CircuitPanel circuitPanel) {
        this.car = car;
        this.circuitPanel = circuitPanel;
    }

    @Override
    protected Void doInBackground() throws Exception {

        while (isRunning) {

            long startTime = System.currentTimeMillis();

            // updates the position of the car
            try {
                car.updatePosition();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // refreshes the screen with the new position
            circuitPanel.repaint();

            // wait for the next screen update
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTimeToScreenUpdate = SCREEN_UPDATE - elapsedTime;
            Thread.sleep(remainingTimeToScreenUpdate);
        }

        return null;
    }
}
