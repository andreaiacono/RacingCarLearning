package me.andreaiacono.racinglearning.rl;

import me.andreaiacono.racinglearning.core.Command;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.misc.CommandsTranslator;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class RacingMDP implements MDP<ScreenFrameState, Integer, CarActionSpace> {

    private ObservationSpace<ScreenFrameState> observationSpace = new ArrayObservationSpace(new int[] {1});
    private CarActionSpace carActionSpace = new CarActionSpace();
    private Game game;
    private byte[] screenBuffer;

    public RacingMDP(Game game) {
        this.game = game;
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
        screenBuffer = game.getScreenFrame();
        return new ScreenFrameState(screenBuffer);
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<ScreenFrameState> step(Integer action) {
        Command command = CommandsTranslator.getCommandFromInteger(action);
        int reward = game.move(command);
        return new StepReply(new ScreenFrameState(screenBuffer), reward, game.isOver(), null);
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
