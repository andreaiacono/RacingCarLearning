package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.GraphFrame;
import me.andreaiacono.racinglearning.gui.TrackPanel;

import static me.andreaiacono.racinglearning.rl.RacingQL.MAX_MOVES_PER_EPOCH;

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
        this.trackDuration = params.getInt(GameParameters.TRACK_DURATION, Integer.MAX_VALUE);
        this.params = params;
        if (trackDuration == 0) {
            trackDuration = Integer.MAX_VALUE;
        }
        hasGraph = params.getBool(GameParameters.SHOW_GRAPH_PARAM);
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
        track.reset();
    }

    public boolean isOver() {
        return track.isCarOutsideScreen() || movesNumber >= MAX_MOVES_PER_EPOCH;
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

        double reward = track.getReward();
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
