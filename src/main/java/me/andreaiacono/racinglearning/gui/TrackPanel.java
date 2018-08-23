package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.track.RandomRaceTrack;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Random;

public class TrackPanel extends JPanel {

    private static final Color CAR_COLOR = Color.RED;
    public static final int CAR_STARTING_ANGLE = 0;
    private static final int TILES_SIDE_NUMBER = 5;
    private final int size;

    private boolean drawInfo;

    // the racing track with the car drawn on it
    private BufferedImage trackImage;
    private final Car car;
    private float scale;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    private BufferedImage racingImage;
    private long time;
    private Stroke thickStroke = new BasicStroke(3f);
    private long startTime;
    private double carWidth;
    private double carLength;
    private final static Random random = new Random(123);

    public TrackPanel(Car car, DrivingKeyListener listener, int size, GameParameters gameParameters, float scale) {

        this.drawInfo = gameParameters.getBool(GameParameters.DRAW_INFO_PARAM);
        this.size = size;

        this.car = car;
        this.scale = scale;
        setFocusable(true);
        addKeyListener(listener);

        Point startingPosition = new Point(size / 2, (size / TILES_SIDE_NUMBER)/2);
        car.setStartingPosition(startingPosition);
        car.setMaxSpeed(Car.SMALL_MAX_SPEED);

        carWidth = 5;
        carLength = 3;

        createNew();
    }


    public void createNew() {
        trackImage = new RandomRaceTrack().getRandomTrack(size, TILES_SIDE_NUMBER, random.nextInt());
        racingImage = new BufferedImage(trackImage.getWidth(), trackImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        car.reset();
        startTime = System.currentTimeMillis();
    }

    /**
     * Computes the rewards associated to the position of the car in the track.
     * If the car is on the track, the reward will be high; if the car is outside the
     * track, the reward will be low.
     *
     * @return the reward for the current position of the car
     */
    public long getReward() {

        if (!isCarInsideScreen()) {
            return -10000;
        }

        // the faster the car goes, the better
        int reward = (int) car.getVelocity().speed;

        if (car.getVelocity().speed == 0) {
            return -100;
        }

//        // the more checkpoints passed in order, the more reward gained
//        reward += getCheckPointsReward();

        // being on track is a lot better than being off track
        reward += isCarOnTrack() ? 1000 : -100;

//        // the more time passes, the worse is  /// MISLEADING!
//        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
//        reward += 20 * elapsedTime;

        return reward;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // creates the image where to draw
        Graphics2D imageGraphics = (Graphics2D) racingImage.getGraphics();

        // draws the track and the car
        imageGraphics.drawImage(trackImage, 0, 0, null);
        drawCar(imageGraphics);

        // draws info
        if (drawInfo) {
            String time = addZeroIfNeeded(this.time / 1000) + ":" + addZeroIfNeeded((this.time % 1000) / 10);
            System.out.print("\r[" + time + "] REWARD: " + getReward() + " - CAR:" + car.toString());
//            imageGraphics.setFont(INFO_FONT);
//            imageGraphics.drawString(car.toString(), 10, 10);
//            imageGraphics.drawString("REWARD: " + getReward(), 10, 20);
//            imageGraphics.drawString("Checks: " + checkSteps.toString(), 10, 30);
//            imageGraphics.drawString("TIME: " + time, 10, 40);
        }

        // draws the image to the panel
        g.drawImage(racingImage.getScaledInstance((int) (size * scale), (int) (size * scale), Image.SCALE_FAST), 0, 0, null);
    }

    public void drawCar(Graphics2D imageGraphics) {

        // computes and draws the car
        double cx = car.getX();
        double cy = car.getY();
        double angleLeft = car.getDirection() - carWidth;
        double angleRight = car.getDirection() + carWidth;

        double carSpeedLength = carLength + car.getVelocity().speed;

        double cosAngleLeft = Math.cos(Math.toRadians(angleLeft)) * carSpeedLength;
        double sinAngleLeft = Math.sin(Math.toRadians(angleLeft)) * carSpeedLength;
        double cosAngleRight = Math.cos(Math.toRadians(angleRight)) * carSpeedLength;
        double sinAngleRight = Math.sin(Math.toRadians(angleRight)) * carSpeedLength;
        double cosAngleBackLeft = Math.cos(Math.toRadians((angleLeft - 180) % 360)) * carSpeedLength;
        double sinAngleBackLeft = Math.sin(Math.toRadians((angleLeft - 180) % 360)) * carSpeedLength;
        double cosAngleBackRight = Math.cos(Math.toRadians((angleRight - 180) % 360)) * carSpeedLength;
        double sinAngleBackRight = Math.sin(Math.toRadians((angleRight - 180) % 360)) * carSpeedLength;

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

    private String addZeroIfNeeded(long value) {
        return value < 10 ? "0" + value : "" + value;
    }

    public byte[] getCurrentFrame() {
        WritableRaster raster = racingImage.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    public boolean isCarInsideScreen() {
        return car.getX() >= 0 && car.getY() >= 0 && car.getX() < size && car.getY() < size;
    }

    public boolean isCarOnTrack() {
        if (!isCarInsideScreen()) {
            return false;
        }
        int pixel = trackImage.getRGB(car.getX(), car.getY());

        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return red == 0 && green == 0 && blue == 0;
    }

    public void updateCircuit() {
        updateCircuit(-1);
    }

    public void updateCircuit(long raceStartTime) {
        this.time = System.currentTimeMillis() - raceStartTime;
        repaint();
    }

    public int getScreenHeight() {
        return racingImage.getHeight();
    }

    public int getScreenWidth() {
        return racingImage.getWidth();
    }

    /**
     * checks that every check point is reached within its time limit.
     *
     * @return
     */
    public boolean isTimeOver() {

        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000;
        return elapsedTime > 20;
    }

    public int getSizeInPixel() {
        return size;
    }

}
