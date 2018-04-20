package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.GraphFrame;
import me.andreaiacono.racinglearning.gui.TrackPanel;

public class Game {

    private int epoch;
    private long epochReward;
    public final Car car;
    public final TrackPanel track;
    private GraphFrame graphFrame;
    private boolean hasGraph;

    public Game(Car car, TrackPanel track, GameParameters params) {
        this.car = car;
        this.track = track;
        hasGraph = params.getValue(GameParameters.TYPE_PARAM).equals(GameParameters.Type.MACHINE_LEARN.toString());
        if (hasGraph) {
            this.graphFrame = new GraphFrame();
        }
    }

    public byte[] getScreenFrame() {
        return track.getCurrentFrame();
    }

    public void reset() {
        System.out.println("Epoch #" + epoch++);
        if (hasGraph) {
            graphFrame.addValue(epochReward);
            epochReward = 0;
        }
        car.reset();
        track.reset();
    }

    public boolean isOver() {
        return !track.isCarInsideScreen() || track.isLapCompleted() || track.isTimeOver();
    }

    public int move(Command command) {

        car.applyCommand(command);

        // updates the position of the car
        car.setIsOnTrack(track.isCarOnTrack());
        car.updatePosition();

        // refreshes the screen with the new position
        track.updateCircuit();

        epochReward += track.getReward();
        return track.getReward();
    }

    public int getScreenWidth() {
        return track.getScreenWidth();
    }

    public int getScreenHeight() {
        return track.getScreenHeight();
    }
}
