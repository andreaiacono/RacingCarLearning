package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.Car;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class CircuitPanel extends JPanel  {

    Image circuitBackground;
    Car car;

    public CircuitPanel(Car car) throws Exception {
        circuitBackground = ImageIO.read(ClassLoader.getSystemResource("background.png"));

        this.car = car;
        setFocusable(true);
        addKeyListener(new DrivingKeyListener(car));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(circuitBackground, 0, 0, null);

        // draws car
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.LIGHT_GRAY  );
        g2d.fillRect(car.getX(), car.getY(), 25, 10);
        g2d.drawString("speed: " + car.getSpeed() + " " + car.getHeading(), car.getX(), car.getY());
    }

}
