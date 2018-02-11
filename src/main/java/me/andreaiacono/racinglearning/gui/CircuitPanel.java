package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.Car;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CircuitPanel extends JPanel {

    private final Image circuitBackground;
    private final Car car;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    private int counter;
    private BufferedImage bufferedImage;

    public CircuitPanel(Car car, DrivingKeyListener listener) throws Exception {
        circuitBackground = ImageIO.read(ClassLoader.getSystemResource("background.png"));

        this.car = car;
        setFocusable(true);
        addKeyListener(listener);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // creates the image where to draw
        bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics imageGraphics = bufferedImage.getGraphics();

        // draws the circuit and the car
        imageGraphics.drawImage(circuitBackground, 0, 0, null);
        imageGraphics.setColor(Color.LIGHT_GRAY);
        imageGraphics.fillRect(car.getX(), car.getY(), 25, 10);
        imageGraphics.drawString(car.toString(), car.getX(), car.getY());

        // draws the image to the panel
        g.drawImage(bufferedImage, 0, 0, null);

        // optionally saves the image to a file
        if (false) {
            try {
                ImageIO.write(bufferedImage, "JPEG", new File(String.format("src/main/resources/RACE_%d_%s.jpg", (counter++), sdf.format(new Date()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Image getImage() {
        return bufferedImage;
    }
}
