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
import java.util.Random;

public class TrackPanel extends JPanel {

    private static final Color CAR_BODY_COLOR = new Color(255, 180, 255);
    private static final Color CAR_HEAD_COLOR = Color.WHITE;
    public static final int CAR_STARTING_ANGLE = 0;
    private static final int TILES_SIDE_NUMBER = 4;
    private final int size;

    private boolean drawInfo;

    // the racing track with the car drawn on it
    private BufferedImage trackImage;
    private final Car car;
    private float scale;
    private BufferedImage trackRaceImage;
    private long time;
    private Stroke carStrokeSize;
    private final static Random random = new Random(1520);

    public TrackPanel(Car car, DrivingKeyListener listener, int size, GameParameters gameParameters, float scale) {

        this.drawInfo = gameParameters.getBool(GameParameters.DRAW_INFO_PARAM);
        this.size = size;

        this.car = car;
        this.scale = scale;
        setFocusable(true);
        addKeyListener(listener);

        Point startingPosition = new Point(size / 2, (size / TILES_SIDE_NUMBER) / 2);
        car.setStartingPosition(startingPosition);
        car.setMaxSpeed(size / 30);

        carStrokeSize = new BasicStroke(size / 20);

        createNew();
    }


    public void createNew() {
        // the image of the track (used for checking if the car is on the track or not)
        trackImage = new RandomRaceTrack().getRandomTrack(size, TILES_SIDE_NUMBER, random.nextInt());

        // the image of the track AND the car (based on trackImage)
        trackRaceImage = new BufferedImage(trackImage.getWidth(), trackImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        car.reset();
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
            return -500;
        }

        // the faster the car goes, the better
        int reward = (int) car.getVelocity().speed;

//        if (car.getVelocity().speed == 0) {
//            return 0;
//        }

//        // the more checkpoints passed in order, the more reward gained
//        reward += getCheckPointsReward();

        // being on track is a lot better than being off track
        reward += isCarOnTrack() ? 1 : -2;

//        // the more time passes, the worse is  /// MISLEADING!
//        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
//        reward += 20 * elapsedTime;

        return reward;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // the trackRaceImage is updated in update

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
        g.drawImage(trackRaceImage.getScaledInstance((int) (size * scale), (int) (size * scale), Image.SCALE_FAST), 0, 0, null);
    }


    public void drawCar(Graphics2D imageGraphics) {

        // computes and draws the car
        double cx = car.getX();
        double cy = car.getY();
        double carTailDirection = (car.getDirection() - 180) % 360;
        double carLength = Math.abs(size / 15);
//        double carLength = Math.abs(car.getVelocity().speed*1.5);

        double cosAngle = Math.cos(Math.toRadians(carTailDirection)) * carLength;
        double sinAngle = Math.sin(Math.toRadians(carTailDirection)) * carLength;

        imageGraphics.setStroke(carStrokeSize);

        // draws the car body
        imageGraphics.setColor(CAR_BODY_COLOR);
        imageGraphics.drawLine((int) cx, (int) cy, (int) (cx + cosAngle*1.01), (int) (cy + sinAngle*1.01));

//        // draws the car head
//        imageGraphics.setColor(CAR_HEAD_COLOR);
//        imageGraphics.drawLine((int) cx, (int) cy, (int) cx, (int) cy);
    }

    private String addZeroIfNeeded(long value) {
        return value < 10 ? "0" + value : "" + value;
    }

    public byte[] getCurrentFrame() {
        WritableRaster raster = trackRaceImage.getRaster();
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

    public void updateTrack() {

        // creates the image where to draw
        Graphics2D imageGraphics = (Graphics2D) trackRaceImage.getGraphics();

        // draws the track and the car
        imageGraphics.drawImage(trackImage, 0, 0, null);
        drawCar(imageGraphics);

        repaint();
        //paintImmediately(0, 0, getWidth(), getHeight());
    }

    public int getScreenHeight() {
        return trackRaceImage.getHeight();
    }

    public int getScreenWidth() {
        return trackRaceImage.getWidth();
    }

    public int getSizeInPixel() {
        return size;
    }

}
