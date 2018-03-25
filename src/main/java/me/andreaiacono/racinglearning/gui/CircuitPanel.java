package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.misc.GameParameter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

public class CircuitPanel extends JPanel {

    private static final int CAR_LENGTH = 4;
    private static final int CAR_WIDTH = 10;
    private static final Color CAR_COLOR = Color.RED;
    private static final Font INFO_FONT = new Font("Arial", Font.PLAIN, 10);
    public static final Point CAR_STARTING_POSITION = new Point(305, 160);


    private int IMAGE_WIDTH;
    private int IMAGE_HEIGHT;

    // the points of the tracks that car has to pass into in order
    // to consider a valid lap (otherwise it could get shortcuts)
    private Rectangle[] checkPoints = {
            new Rectangle(312, 143, 8, 32),
            new Rectangle(372, 108, 27, 10),
            new Rectangle(262, 12, 13, 33),
            new Rectangle(201, 102, 12, 35),
            new Rectangle(96, 24, 10, 35),
            new Rectangle(1, 112, 32, 10),
            new Rectangle(43, 140, 10, 31),
            new Rectangle(79, 64, 10, 30)
    };

    private BitSet checkSteps = new BitSet(checkPoints.length);

    private static final String CIRCUIT_FILENAME = "circuit.png";
    private static final String REWARD_CIRCUIT_FILENAME = "reward_circuit.png";
    private boolean drawInfo;
    private final BufferedImage circuitImage;
    private final BufferedImage rewardCircuitImage;
    private final Car car;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    private int counter;
    private BufferedImage bufferedImage;
    private long time;
    private boolean isLapCompleted = false;
    private Stroke thickStroke = new BasicStroke(2.5f);

    public CircuitPanel(Car car, DrivingKeyListener listener, GameParameters gameParameters) throws Exception {
        this.drawInfo = gameParameters.getBool(GameParameter.DRAW_INFO);
        circuitImage = gameParameters.getBool(GameParameter.USE_BLACK_AND_WHITE)
                ? ImageIO.read(ClassLoader.getSystemResource(REWARD_CIRCUIT_FILENAME))
                : ImageIO.read(ClassLoader.getSystemResource(CIRCUIT_FILENAME));
        rewardCircuitImage = ImageIO.read(ClassLoader.getSystemResource(REWARD_CIRCUIT_FILENAME));

        bufferedImage = new BufferedImage(circuitImage.getWidth(), circuitImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        IMAGE_WIDTH = circuitImage.getWidth();
        IMAGE_HEIGHT = circuitImage.getHeight();

        this.car = car;
        setFocusable(true);
        addKeyListener(listener);
    }

    /**
     * Computes the rewards associated to the position of the car in the circuit.
     * If the car is on the track, the reward will be high; if the car is outside the
     * track, the reward will be low. Also, the more checkpoints passed, the better.
     *
     * @return the reward for the current position of the car (based on the path it took to arrive there)
     */
    public int getReward() {

        if (!isCarInsideScreen()) {
            return -1000;
        }

        // the faster the car goes, the better
        int reward = (int) car.getVelocity().speed * 10;

        // the more checkpoints passed, the more reward gained
        reward += checkSteps.cardinality() * 50;

        // being on track is better than being off track
        reward += isCarOnTrack() ? 100 : -100;

        // if the lap was completed, super reward!
        if (isLapCompleted) {
            reward += 10000;
        }

        return reward;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateCheckpoints();

        // creates the image where to draw
        Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();

        // draws the circuit and the car
        imageGraphics.drawImage(circuitImage, 0, 0, null);
        drawCar(imageGraphics);

        // draws info
        if (drawInfo) {
            String time = addZeroIfNeeded(this.time / 1000) + ":" + addZeroIfNeeded((this.time % 1000) / 10);
            imageGraphics.setFont(INFO_FONT);
            imageGraphics.drawString(car.toString(), 10, 10);
            imageGraphics.drawString("REWARD: " + getReward(), 10, 20);
            imageGraphics.drawString("Checks: " + checkSteps.toString(), 230, 10);
            imageGraphics.drawString("TIME: " + time, 230, 20);
        }

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

    private void updateCheckpoints() {
        boolean areAllSet = true;
        for (int i = 0; i < checkPoints.length; i++) {
            if (checkPoints[i].contains(car.getX(), car.getY())) {
                checkSteps.set(i);
            }
            if (!checkSteps.get(i)) {
                areAllSet = false;
            }
        }

        if (areAllSet && checkPoints[0].contains(car.getX(), car.getY())) {
            isLapCompleted = true;
        }
    }

    private String addZeroIfNeeded(long value) {
        return value < 10 ? "0" + value : "" + value;
    }

    public void drawCar(Graphics2D imageGraphics) {

        // computes and draws the car
        double cx = car.getX();
        double cy = car.getY();
        double angleLeft = car.getDirection() - CAR_WIDTH;
        double angleRight = car.getDirection() + CAR_WIDTH;

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
        imageGraphics.setStroke(thickStroke);
        imageGraphics.drawLine((int) (cx + cosAngleLeft), (int) (cy + sinAngleLeft), (int) (cx + cosAngleRight), (int) (cy + sinAngleRight));
    }

    public byte[] getCurrentFrame() {
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        System.out.println("BUFFER SIZE = " + buffer.getData().length);
        return buffer.getData();
    }

    public boolean isCarInsideScreen() {
        return car.getX() >= 0 && car.getY() >= 0 && car.getX() <= IMAGE_WIDTH && car.getY() <= IMAGE_HEIGHT;
    }

    public boolean isCarOnTrack() {
        if (!isCarInsideScreen()) {
            return false;
        }
        int color = rewardCircuitImage.getRGB(car.getX(), car.getY());
        return color == -1;
    }

    public boolean isLapCompleted() {
        return isLapCompleted;
    }

    public void updateCircuit() {
        updateCircuit(-1);
    }

    public void updateCircuit(long raceStartTime) {
        this.time = System.currentTimeMillis() - raceStartTime;
        repaint();
    }

    public void reset() {

    }

    public int getScreenHeight() {
        return bufferedImage.getHeight();
    }

    public int getScreenWidth() {
        return bufferedImage.getWidth();
    }
}
