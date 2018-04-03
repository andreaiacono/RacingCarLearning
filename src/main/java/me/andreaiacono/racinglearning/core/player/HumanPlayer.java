package me.andreaiacono.racinglearning.core.player;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.gui.TrackPanel;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;

public class HumanPlayer {

    // the Hertz the game is running at
    private final static int GAME_FREQUENCY = 20;

    private final static long ONE_SECOND_IN_MILLIS = 1_000;
    private final static long SCREEN_UPDATE = ONE_SECOND_IN_MILLIS / GAME_FREQUENCY;


    private final Game game;
    private DrivingKeyListener listener;

    public HumanPlayer(Game game, DrivingKeyListener listener) {
        this.game = game;
        this.listener = listener;
    }

    // the game loop
    public void race(long raceStartTime) throws Exception {

        TrackPanel circuit = game.track;
        Car car = game.car;

        while (!circuit.isLapCompleted()) {

            long startTime = System.currentTimeMillis();

            // updates the position of the car
            car.setIsOnTrack(circuit.isCarOnTrack());
            car.updatePosition();

            // refreshes the screen with the new position
            circuit.updateCircuit(raceStartTime);

            car.applyDirections(listener.getCommand());
            if (!circuit.isCarInsideScreen()) {
                break;
            }

            // wait for the next screen updatePosition
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTimeToScreenUpdate = Math.max(0, SCREEN_UPDATE - elapsedTime);
            Thread.sleep(remainingTimeToScreenUpdate);
        }
    }
}
