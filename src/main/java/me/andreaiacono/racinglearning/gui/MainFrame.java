package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;

import javax.swing.*;

public class MainFrame extends JFrame {

    private final Car car;
    private final GameLoopWorker gameLoopWorker;
    private final DrivingKeyListener listener = new DrivingKeyListener();

    public MainFrame() throws Exception {

        super("Racing Car Learning");
        setSize(800, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // creates the car
        car = new Car(610, 320);

        // creates and adds the circuit to this window
        CircuitPanel panel = new CircuitPanel(car, listener);
        panel.setFocusable(true);
        add(panel);

        // starts the game
        gameLoopWorker = new GameLoopWorker(car, panel, listener);
        gameLoopWorker.execute();

        setVisible(true);
    }

}
