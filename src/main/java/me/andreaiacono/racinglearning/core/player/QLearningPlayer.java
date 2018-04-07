package me.andreaiacono.racinglearning.core.player;

import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.rl.QLearning;

public class QLearningPlayer {

    private final Game game;

    public QLearningPlayer(Game game) {
        this.game = game;
    }

    public void learn(String modelName) throws Exception {
        new QLearning(game).learn(modelName);
    }

    public void race(String modelName) throws Exception {
        new QLearning(game).race(modelName);
    }
}
