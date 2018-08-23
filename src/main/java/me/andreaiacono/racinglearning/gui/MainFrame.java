package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;

import javax.swing.*;

public class MainFrame extends JFrame {

    private final Car car;
    private final GameWorker gameWorker;
    private final DrivingKeyListener listener = new DrivingKeyListener();

    public MainFrame(GameParameters params) throws Exception {

        super("Racing Car - " + params.getValue(GameParameters.TYPE_PARAM));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        int size = params.isProvided(GameParameters.SIZE_PARAM) ?
            Integer.parseInt(params.getValue(GameParameters.SIZE_PARAM))
            : 100;

        // creates the car
        car = new Car();

        // creates and adds the track to this window
        TrackPanel panel = new TrackPanel(car, listener, size, params);
        Game game = new Game(car, panel, params);

        panel.setFocusable(true);
        add(panel);
        setSize(size, size+20);

        // starts the game
        gameWorker = new GameWorker(game, params, listener);
        gameWorker.execute();

        setVisible(true);
    }

}
