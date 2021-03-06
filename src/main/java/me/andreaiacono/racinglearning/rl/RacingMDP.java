package me.andreaiacono.racinglearning.rl;

import me.andreaiacono.racinglearning.core.Command;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.misc.CommandsTranslator;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.text.DecimalFormat;

public class RacingMDP implements MDP<ScreenFrameState, Integer, CarActionSpace> {

    private DecimalFormat decimalFormat = new DecimalFormat("0.000");
    private final ObservationSpace<ScreenFrameState> observationSpace;
    private CarActionSpace carActionSpace = new CarActionSpace();
    private Game game;
    private byte[] screenBuffer;
    private long epoch = 1;

    RacingMDP(Game game) {
        this.game = game;
        int[] shape = {
                3,
                game.track.getScreenHeight(),
                game.track.getScreenWidth()
        };
        observationSpace = new ArrayObservationSpace<>(shape);
    }

    @Override
    public ObservationSpace<ScreenFrameState> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public CarActionSpace getActionSpace() {
        return carActionSpace ;
    }

    @Override
    public ScreenFrameState reset() {
        game.reset();
        epoch++;
        screenBuffer = game.getScreenFrame();
        System.out.println();
        return new ScreenFrameState(observationSpace.getShape(), screenBuffer);
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<ScreenFrameState> step(Integer action) {
        Command command = CommandsTranslator.getCommandFromInteger(action);
        double reward = game.move(command);
        System.out.print("\rEpoch #" + epoch + " - Executed command #" + game.getMovesNumber() + " [" + command + "] - Reward: " + reward + " - Cumulative Reward: " + decimalFormat.format(game.getCumulativeReward()) + " - Total moves: " + game.getCumulativeMovesNumber() + "\t\t\t\t\t\t");
        return new StepReply<>(new ScreenFrameState(observationSpace.getShape(), screenBuffer), reward, game.isOver(), null);
    }

    @Override
    public boolean isDone() {
        return game.isOver();
    }

    @Override
    public MDP<ScreenFrameState, Integer, CarActionSpace> newInstance() {
        game.reset();
        return new RacingMDP(game);
    }
}
