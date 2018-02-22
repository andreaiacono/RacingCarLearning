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

    private static final int CAR_LENGTH = 12;
    private static final int CAR_WIDTH = 28;
    private static final Color CAR_COLOR = Color.RED;

    private static final String CIRCUIT_FILENAME = "circuit.png";
    private static final String REWARD_CIRCUIT_FILENAME = "reward_circuit.png";
    private final Image circuitImage;
    private final BufferedImage rewardCircuitImage;
    private final Car car;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    private int counter;
    private BufferedImage bufferedImage;

    public CircuitPanel(Car car, DrivingKeyListener listener) throws Exception {
        circuitImage = ImageIO.read(ClassLoader.getSystemResource(CIRCUIT_FILENAME));
        rewardCircuitImage = ImageIO.read(ClassLoader.getSystemResource(REWARD_CIRCUIT_FILENAME));

        this.car = car;
        setFocusable(true);
        addKeyListener(listener);
    }

    /**
     * Computes the rewards associated to the position of the car in the circuit.
     * If the car is on the track, the reward will be high; if the car is outside the
     * track, the reward will be low
     *
     * @return 10 if on track and -10 if not on track
     */
    public int getReward() {
        int color = rewardCircuitImage.getRGB(car.getX(), car.getY());
        return color == -1 ? 10 : -10;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // creates the image where to draw
        bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics imageGraphics = bufferedImage.getGraphics();

        // draws the circuit
        imageGraphics.drawImage(circuitImage, 0, 0, null);
        imageGraphics.drawString(car.toString(), 10, 20);
        imageGraphics.drawString("REWARD: " + getReward(), 10, 40);

        // computes and draws the car
        double cx = car.getX();
        double cy = car.getY();
        double angleLeft = car.getDirection() - CAR_WIDTH;
        double angleCenter = car.getDirection();
        double angleRight = car.getDirection() + CAR_WIDTH;

        double cosAngleCenter = Math.cos(Math.toRadians(angleCenter)) * (CAR_LENGTH * .8);
        double sinAngleCenter = Math.sin(Math.toRadians(angleCenter)) * (CAR_LENGTH * .8);
        double cosAngleLeft = Math.cos(Math.toRadians(angleLeft)) * CAR_LENGTH;
        double sinAngleLeft = Math.sin(Math.toRadians(angleLeft)) * CAR_LENGTH;
        double cosAngleRight = Math.cos(Math.toRadians(angleRight)) * CAR_LENGTH;
        double sinAngleRight = Math.sin(Math.toRadians(angleRight)) * CAR_LENGTH;
        double cosAngleBackLeft = Math.cos(Math.toRadians((angleLeft - 180) % 360)) * CAR_LENGTH;
        double sinAngleBackLeft = Math.sin(Math.toRadians((angleLeft - 180) % 360)) * CAR_LENGTH;
        double cosAngleBackRight = Math.cos(Math.toRadians((angleRight - 180) % 360)) * CAR_LENGTH;
        double sinAngleBackRight = Math.sin(Math.toRadians((angleRight - 180) % 360)) * CAR_LENGTH;

        Polygon drawnCar = new Polygon();
        Point firstPoint = new Point((int) (cx + cosAngleLeft), (int) (cy + sinAngleLeft));
        drawnCar.addPoint(firstPoint.x, firstPoint.y);
        drawnCar.addPoint((int) (cx + cosAngleRight), (int) (cy + sinAngleRight));
        drawnCar.addPoint((int) (cx + cosAngleBackLeft), (int) (cy + sinAngleBackLeft));
        drawnCar.addPoint((int) (cx + cosAngleBackRight), (int) (cy + sinAngleBackRight));
        drawnCar.addPoint(firstPoint.x, firstPoint.y);
        imageGraphics.setColor(CAR_COLOR);
        imageGraphics.fillPolygon(drawnCar);

        imageGraphics.setColor(Color.YELLOW);
        imageGraphics.fillOval((int) (cx + cosAngleCenter), (int) (cy + sinAngleCenter), 5, 5);

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

    public boolean isCarOutsideCircuit() {
//        return car.getX() < 0 || car.getY() < 0 || car.getX() > bufferedImage.getWidth() || car.getY() > bufferedImage.getHeight();
        return car.getX() < 0 || car.getY() < 0;
    }
}
