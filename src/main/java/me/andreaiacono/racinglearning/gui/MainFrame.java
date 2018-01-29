package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() throws Exception {

        super("Racing Car Learning");

        setSize(800, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Car car = new Car(100,100);
        CircuitPanel panel = new CircuitPanel(car);
        panel.setFocusable(true);
        add(panel);
        setVisible(true);
    }

}
