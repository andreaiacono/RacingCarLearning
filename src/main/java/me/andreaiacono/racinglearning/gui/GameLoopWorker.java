package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.core.Lap;
import me.andreaiacono.racinglearning.core.player.HumanPlayer;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.player.QLearningPlayer;
import me.andreaiacono.racinglearning.rl.ReinforcementLearningManager;

import javax.swing.*;

import static me.andreaiacono.racinglearning.misc.GameParameter.IS_HUMAN;

public class GameLoopWorker extends SwingWorker<Void, Void> {

    private final ReinforcementLearningManager rl = new ReinforcementLearningManager();

    private Lap lap;
    private DrivingKeyListener listener;
    private GameParameters params;

    public GameLoopWorker(Lap lap, GameParameters params, DrivingKeyListener listener) {
        this.lap = lap;
        this.listener = listener;
        this.params = params;
    }

    @Override
    protected Void doInBackground() throws Exception {

        // car driven by a human player
        if (params.getBool(IS_HUMAN)) {
            long raceStartTime = System.currentTimeMillis();
            new HumanPlayer(lap, listener).race(raceStartTime);

            if (lap.circuit.isLapCompleted()) {
                System.out.println("LAP SUCCESSFULLY COMPLETED IN " + (System.currentTimeMillis() - raceStartTime) + "ms");
            }
            else {
                // if the car went out of the screen
                System.out.println("LAP NOT COMPLETED");
            }
        }
        // car driven by the RL algorithm
        else {
            new QLearningPlayer(lap).race(lap);
        }

        System.exit(0);
        return null;
    }
}
