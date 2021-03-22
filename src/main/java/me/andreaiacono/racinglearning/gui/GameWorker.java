package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.core.player.HumanPlayer;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.player.QLearningPlayer;
import me.andreaiacono.racinglearning.core.GameParameters.Type;

import javax.swing.*;
import java.util.Locale;

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
            Type type = Type.valueOf(params.getValue(GameParameters.TYPE_PARAM).toUpperCase(Locale.ROOT));

            // car driven by a human player
            if (type == Type.HUMAN) {
                long raceStartTime = System.currentTimeMillis();
                new HumanPlayer(game, listener).race(raceStartTime);

//                if (game.track.isLapCompleted()) {
//                    System.out.println("LAP SUCCESSFULLY COMPLETED IN " + (System.currentTimeMillis() - raceStartTime) + "ms");
//                } else {
//                    // if the car went out of the screen
//                    System.out.println("LAP NOT COMPLETED");
//                }
            }
            // RL train of driving a car
            else if (type == Type.TRAIN) {
                if (modelName == null) {
                    System.out.println("The model name is needed. (-m argument).");
                    System.exit(-1);
                }
                new QLearningPlayer(game).learn(modelName);

            }
            // machine race using a previously trained model
            else if (type == Type.RACE) {
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
