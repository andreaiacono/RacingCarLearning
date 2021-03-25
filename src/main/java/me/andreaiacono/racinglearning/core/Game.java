package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.GraphFrame;
import me.andreaiacono.racinglearning.gui.TrackPanel;

import java.awt.image.BufferedImage;

import static me.andreaiacono.racinglearning.misc.Constants.MAX_MOVES_NUMBER;

public class Game {

    private int trackDuration;
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
        this.trackDuration = Integer.MAX_VALUE;
        if (trackDuration == 0) {
            trackDuration = Integer.MAX_VALUE;
        }
        hasGraph = params.getBool(GameParameters.SHOW_GRAPH_PARAM);
        if (params.getBool(GameParameters.SHOW_GRAPH_PARAM)) {
            this.graphFrame = new GraphFrame();
        }
    }

    public byte[] getScreenFrame() {
        return track.getCurrentFrame();
    }

    public BufferedImage getCurrentImage() {
        return track.getCurrentImage();
    }

    public void reset() {
        epoch ++;
        if (hasGraph) {
            graphFrame.addValue(epochReward, movesNumber);
            epochReward = 0;
        }
        movesNumber = 0;
        car.reset();
        track.reset();
        if (epoch % trackDuration == 0) {
            track.createNew();
        }
    }

    public boolean isOver() {
        return track.isCarOutsideScreen() || movesNumber > MAX_MOVES_NUMBER || track.isLapCompleted;
    }

    public float move(Command command) {
        movesNumber++;
        cumulativeMovesNumber++;
        car.applyCommand(command);

        // updates the position of the car
        car.setIsOnTrack(track.isCarOnTrack());
        car.updatePosition();

        // refreshes the screen with the new position
        track.updateTrack();

//        try {
//            Thread.sleep(30);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        float reward = track.getReward();
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

    public long getEpochReward() {
        return (long) epochReward;
    }

    public int getCumulativeMovesNumber() {
        return cumulativeMovesNumber;
    }

    public int getPanelSize() {
        return track.getSizeInPixel();
    }
}
