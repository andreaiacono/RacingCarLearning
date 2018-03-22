package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.core.player.HumanPlayer;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.player.QLearningPlayer;

import javax.swing.*;

import static me.andreaiacono.racinglearning.misc.GameParameter.IS_HUMAN;

public class GameWorker extends SwingWorker<Void, Void> {

    private Game game;
    private DrivingKeyListener listener;
    private GameParameters params;

    public GameWorker(Game game, GameParameters params, DrivingKeyListener listener) {
        this.game = game;
        this.listener = listener;
        this.params = params;
    }

    @Override
    protected Void doInBackground() throws Exception {

        // car driven by a human player
        if (params.getBool(IS_HUMAN)) {
            long raceStartTime = System.currentTimeMillis();
            new HumanPlayer(game, listener).race(raceStartTime);

            if (game.circuit.isLapCompleted()) {
                System.out.println("LAP SUCCESSFULLY COMPLETED IN " + (System.currentTimeMillis() - raceStartTime) + "ms");
            }
            else {
                // if the car went out of the screen
                System.out.println("LAP NOT COMPLETED");
            }
        }
        // car driven by the RL algorithm
        else {
            new QLearningPlayer(game).race();
        }

        System.exit(0);
        return null;
    }
}
