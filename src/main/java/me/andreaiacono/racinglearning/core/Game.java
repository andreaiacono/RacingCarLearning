package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.TrackPanel;

public class Game {

    private int epoch;
    public final Car car;
    public final TrackPanel track;

    public Game(Car car, TrackPanel track) {
        this.car = car;
        this.track = track;
    }

    public byte[] getScreenFrame() {
        return track.getCurrentFrame();
    }

    public void reset() {
        System.out.println("Epoch #" + epoch++);
        car.reset();
        track.reset();
    }

    public boolean isOver() {
        return !track.isCarInsideScreen() || track.isLapCompleted() || track.isTimeOver();
    }

    public int move(Command command) {

        car.applyDirections(command);

        // updates the position of the car
        car.setIsOnTrack(track.isCarOnTrack());
        car.updatePosition();

        // refreshes the screen with the new position
        track.updateCircuit();

        return track.getReward();
    }

    public int getScreenWidth() {
        return track.getScreenWidth();
    }

    public int getScreenHeight() {
        return track.getScreenHeight();
    }
}
