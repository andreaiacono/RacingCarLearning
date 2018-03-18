package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.core.Lap;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;

import javax.swing.*;

public class MainFrame extends JFrame {

    private final Car car;
    private final GameLoopWorker gameLoopWorker;
    private final DrivingKeyListener listener = new DrivingKeyListener();

    public MainFrame(String[] args) throws Exception {

        super("Racing Car Learning");
        setSize(400, 220);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // creates the car
        car = new Car(305, 160);

        // creates and adds the circuit to this window
        GameParameters params = new GameParameters(args);
        CircuitPanel panel = new CircuitPanel(car, listener, params);
        Lap lap = new Lap(car, panel);

        panel.setFocusable(true);
        add(panel);

        // starts the game
        gameLoopWorker = new GameLoopWorker(lap, params, listener);
        gameLoopWorker.execute();

        setVisible(true);
    }

}
