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

    public MainFrame(String[] args) throws Exception {

        super("Racing Car Learning");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // creates the car
        car = new Car();

        // creates and adds the track to this window
        GameParameters params = new GameParameters(args);
        TrackPanel panel = new TrackPanel(car, listener, params);
        Game game = new Game(car, panel);

        panel.setFocusable(true);
        add(panel);
        setSize(panel.getScreenWidth(), panel.getScreenHeight()+20);

        // starts the game
        gameWorker = new GameWorker(game, params, listener);
        gameWorker.execute();

        setVisible(true);
    }

}
