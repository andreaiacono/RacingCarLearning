package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.core.player.HumanPlayer;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.player.QLearningPlayer;

import javax.swing.*;

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
    protected Void doInBackground() {

        try {

            String modelName = params.getValue(GameParameters.MODEL_NAME_PARAM);

            // car driven by a human player
            if (params.getValue(GameParameters.TYPE_PARAM).equals(GameParameters.Type.HUMAN.toString())) {
                long raceStartTime = System.currentTimeMillis();
                new HumanPlayer(game, listener).race(raceStartTime);

                if (game.track.isLapCompleted()) {
                    System.out.println("LAP SUCCESSFULLY COMPLETED IN " + (System.currentTimeMillis() - raceStartTime) + "ms");
                } else {
                    // if the car went out of the screen
                    System.out.println("LAP NOT COMPLETED");
                }
            }
            // RL train of driving a car
            else if (params.getValue(GameParameters.TYPE_PARAM).equals(GameParameters.Type.MACHINE_LEARN.toString())) {
                if (modelName == null) {
                    System.out.println("The model name is needed. (-m argument).");
                    System.exit(-1);
                }
                new QLearningPlayer(game).learn(modelName);
            }
            // machine race using a previously trained model
            else if (params.getValue(GameParameters.TYPE_PARAM).equals(GameParameters.Type.MACHINE_RACE.toString())) {
                if (modelName == null) {
                    System.out.println("The model name is needed. (-m argument).");
                    System.exit(-1);
                }
                new QLearningPlayer(game).race(modelName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
        return null;
    }
}
