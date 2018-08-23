package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.gui.GraphFrame;
import me.andreaiacono.racinglearning.gui.TrackPanel;

public class Game {

    private Long trackDuration;
    private int epoch;
    private int moveNumber;
    private long epochReward;
    public final Car car;
    public final TrackPanel track;
    private GraphFrame graphFrame;
    private boolean hasGraph;

    public Game(Car car, TrackPanel track, GameParameters params) {
        this.car = car;
        this.track = track;
        this.trackDuration = params.getValueWithDefault(GameParameters.TRACK_DURATION, 1L);
        if (trackDuration == 0) {
            trackDuration = Long.MAX_VALUE;
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
//        System.out.println("Epoch #" + epoch);
        epoch ++;
        if (hasGraph) {
            graphFrame.addValue(epochReward);
            epochReward = 0;
        }
        moveNumber = 0;
        car.reset();
        if (epoch % trackDuration == 1) {
            track.createNew();
        }
    }

    public boolean isOver() {
        return !track.isCarInsideScreen() || track.isTimeOver();
    }

    public long move(Command command) {

        moveNumber++;
        car.applyCommand(command);

        // updates the position of the car
        car.setIsOnTrack(track.isCarOnTrack());
        car.updatePosition();

        // refreshes the screen with the new position
        track.updateCircuit();

        long reward = track.getReward();
        epochReward += reward;
        return reward;
    }

    public void saveChartImage(String filename) throws Exception {
        if (hasGraph) {
            graphFrame.saveChartAsImage(filename);
        }
    }
}
