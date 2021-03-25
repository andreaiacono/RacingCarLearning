package me.andreaiacono.racinglearning;

import ai.djl.Model;
import ai.djl.ndarray.NDManager;
import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.core.GameParameters.Type;
import me.andreaiacono.racinglearning.core.player.HumanPlayer;
import me.andreaiacono.racinglearning.game.CarEnvironment;
import me.andreaiacono.racinglearning.gui.TrackFrame;
import me.andreaiacono.racinglearning.gui.TrackPanel;

import java.util.Locale;

import static me.andreaiacono.racinglearning.ai.NetworkModel.createOrLoadModel;
import static me.andreaiacono.racinglearning.ai.NetworkTrainer.*;
import static me.andreaiacono.racinglearning.core.GameParameters.Type.*;

public class Main {

    public static void main(String[] args) throws Throwable {
        GameParameters params = new GameParameters(args);
        Type type = Type.valueOf(params.getValue(GameParameters.TYPE_PARAM).toUpperCase(Locale.ROOT));

        Car car = new Car(params.getBool(GameParameters.SIMPLE_CAR_PARAM));
        int screenSize = params.getInt(GameParameters.SCREEN_SIZE_PARAM, 40);
        float magnify = params.getFloat(GameParameters.MAGNIFY_PARAM, 1);
        boolean drawInfo = params.getBool(GameParameters.DRAW_INFO_PARAM);
        float isEasy = params.getFloat(GameParameters.EASY_PARAM, 0);
        boolean drawCheckpoints = params.getBool(GameParameters.DRAW_CHECKPOINTS);
        TrackPanel trackPanel = new TrackPanel(car, drawInfo, drawCheckpoints, screenSize, magnify, isEasy);
        Game game = new Game(car, trackPanel, params);

        if (params.getBool(GameParameters.SHOW_TRACK)) {
            TrackFrame trackFrame = new TrackFrame(game, screenSize, magnify);
            trackFrame.setVisible(true);
        }

        if (type == HUMAN) {
            long raceStartTime = System.currentTimeMillis();
            new HumanPlayer(game).race(raceStartTime);
            if (game.track.isLapCompleted) {
                System.out.println("\nLAP SUCCESSFULLY COMPLETED IN " + (System.currentTimeMillis() - raceStartTime) + "ms");
            } else {
                // if the car went out of the screen
                System.out.println("\nLAP NOT COMPLETED");
            }
            System.exit(0);
        } else {
            int batchSize = params.getInt(GameParameters.BATCH_SIZE_PARAM, 32);
            CarEnvironment carEnvironment = new CarEnvironment(NDManager.newBaseManager(), batchSize, REPLAY_BUFFER_SIZE, game);
            boolean usePretrained = false;
            Model model = createOrLoadModel(usePretrained);

            if (type == RACE) {
                test(model, carEnvironment);
            } else if (type == TRAIN) {
                train(carEnvironment, model, screenSize, batchSize);
            }
        }
    }
}