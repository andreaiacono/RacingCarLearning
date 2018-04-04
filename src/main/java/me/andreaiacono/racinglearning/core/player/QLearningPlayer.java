package me.andreaiacono.racinglearning.core.player;

import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.rl.QLearning;

public class QLearningPlayer {

    private final Game game;

    public QLearningPlayer(Game game) {
        this.game = game;
    }

    public void learn() {
        new QLearning(game).startLearning("racing-dql.net.model");
    }

    public void race() {
        new QLearning(game).race("racing-dql.net.model");
    }
}
