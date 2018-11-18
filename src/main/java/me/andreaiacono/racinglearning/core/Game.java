package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.GraphFrame;
import me.andreaiacono.racinglearning.gui.TrackPanel;

public class Game {

    private int trackDuration;
    private GameParameters params;
    private int epoch;
    private int cumulativeMovesNumber;
    private int movesNumber;
    private double epochReward;
    public final Car car;
    public final TrackPanel track;
    private GraphFrame graphFrame;
    private boolean hasGraph;

    public Game(Car car, TrackPanel track, GameParameters params) {
        this.car = car;
        this.track = track;
        this.trackDuration = params.getValueWithDefault(GameParameters.TRACK_DURATION, Integer.MAX_VALUE);
        this.params = params;
        if (trackDuration == 0) {
            trackDuration = Integer.MAX_VALUE;
        }
        hasGraph = params.getValue(GameParameters.TYPE_PARAM).equals(GameParameters.Type.MACHINE_LEARN.toString());
        if (hasGraph) {
            this.graphFrame = new GraphFrame();
        }
    }

    public byte[] getScreenFrame() {
        return track.getCurrentFrame();
    }

    public void reset() {
        epoch ++;
        if (hasGraph) {
            graphFrame.addValue(epochReward, movesNumber);
            epochReward = 0;
        }
        movesNumber = 0;
        car.reset();
        if (epoch % trackDuration == 0) {
            track.createNew();
        }
    }

    public boolean isOver() {
        return track.isCarOutsideScreen();
    }

    public double move(Command command) {

        movesNumber++;
        cumulativeMovesNumber++;
        car.applyCommand(command);

        // updates the position of the car
        car.setIsOnTrack(track.isCarOnTrack());
        car.updatePosition();

        // refreshes the screen with the new position
        track.updateTrack();

        double reward = track.getReward(movesNumber);
        epochReward += reward;
        return reward;
    }

    public void saveChartImage(String filename) throws Exception {
        if (hasGraph) {
            graphFrame.saveChartAsImage(filename);
        }
    }

    public int getMovesNumber() {
        return movesNumber;
    }

    public double getCumulativeReward() {
        return epochReward;
    }

    public int getCumulativeMovesNumber() {
        return cumulativeMovesNumber;
    }

    public GameParameters getGameParams() {
        return params;
    }
}
