package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.Car;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.track.RandomTrackGenerator;
import me.andreaiacono.racinglearning.track.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static me.andreaiacono.racinglearning.track.RandomTrackGenerator.GRASS_COLOR;

public class TrackPanel extends JPanel {

    private static final Color CAR_BODY_COLOR = new Color(255, 180, 255);
    private static final Color CAR_HEAD_COLOR = Color.YELLOW;
    public static final int CAR_STARTING_ANGLE = 0;

    private final int size;
    private final int tilesNumber;
    private long startTime;

    private boolean drawInfo;
    private boolean drawChekPoints;
    private float easyTrackRatio;

    // the racing track with the car drawn on it
    private BufferedImage trackImage;
    private final Car car;
    private long time;
    private final float scale;
    private BufferedImage trackRaceImage;
    private final Stroke carStrokeSize;
    private final static Random random = new Random(3);
    //    private final static Random random = new Random(1520);
    private float currentReward;
    private float totalReward;
    private final DrivingKeyListener keyListener = new DrivingKeyListener();

    private static final Font INFO_FONT = new Font("Arial", Font.PLAIN, 11);
    private List<Polygon> checkPoints = new ArrayList<>();

    private BitSet checkSteps;
    public boolean isLapCompleted;

    public TrackPanel(Car car, boolean drawInfo, boolean drawChekPoints, int size, float scale, float easyTrackRatio) {

        this.drawInfo = drawInfo;
        this.drawChekPoints = drawChekPoints;
        this.easyTrackRatio = easyTrackRatio;
        this.tilesNumber = 4;
        this.size = size;

        this.car = car;
        this.scale = scale;
        setFocusable(true);
        addKeyListener(keyListener);

        startTime = System.currentTimeMillis();

        Point startingPosition = new Point(size / 2, (size / tilesNumber) / 2);
        car.setStartingPosition(startingPosition);
        car.setMaxSpeed(size / 30);

        carStrokeSize = new BasicStroke((int) (size / (double) 20));

        createNew();
        invalidate();
    }

    public void createNew() {

        if (easyTrackRatio> 0) {
            trackImage = new RandomTrackGenerator().getEasyTrack(size, easyTrackRatio);
            checkPoints = new ArrayList<>();
            checkSteps = new BitSet(0);
        } else {
            Track track = new RandomTrackGenerator().getRandomTrack(size, tilesNumber, 16, drawChekPoints);
            trackImage = track.getImage();
            checkPoints = track.getCheckpoints();
            checkSteps = new BitSet(checkPoints.size());
        }

        // the image used for  the track AND the car (based on trackImage)
        trackRaceImage = new BufferedImage(trackImage.getWidth(), trackImage.getHeight(), RandomTrackGenerator.IMAGE_TYPE);
        car.reset();
    }

    /**
     * Computes the rewards associated to the position of the car in the track.
     * If the car is on the track, the reward will be high; if the car is outside the
     * track, the reward will be low.
     *
     * @return the reward for the current position of the car
     */
    public float getReward() {

//        if (movesNumber == RacingQL.MAX_MOVES_PER_EPOCH) {
//            return 100d;
//        }

        if (isCarOutsideScreen()) {
            currentReward = -10;
        } else {

            //  the faster the car goes, the better
            //  int reward = (int) car.getVelocity().speed;

            //  if (car.getVelocity().speed == 0) {
            //      return 0;
            //  }

            // being on track is a lot better than being off track
            currentReward = isCarOnTrack() ? 0.0f : -0.1f;

            // the more checkpoints passed in order, the more reward gained
            currentReward += getCheckPointsReward();
        }

        totalReward += currentReward;
        return currentReward;

//        // the more time passes, the worse is  /// MISLEADING, it runs out of the screen!
//        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
//        reward += 20 * elapsedTime;

        //lastReward = reward / 500f;
        //return lastReward;
    }

    private void updateCheckpoints() {
        if (checkPoints.size() > 0) {
            boolean areAllSet = true;
            for (int i = 0; i < checkPoints.size(); i++) {
                if (checkPoints.get(i).contains(car.getX(), car.getY())) {
                    checkSteps.set(i);
                }
                if (!checkSteps.get(i)) {
                    areAllSet = false;
                }
            }

            if (areAllSet && checkPoints.get(0).contains(car.getX(), car.getY())) {
                isLapCompleted = true;
            }
        }
    }

    int checkedPoints = 0;

    // the checkpoints give a positive reward only if they're passed in the correct sequence
    private int getCheckPointsReward() {

        // not yet started, neutral reward
        if (checkSteps.length() == 0) {
            return 0;
        }

        int firstNotPassedCheckPoint = -1;

        for (int i = 0; i < checkSteps.length(); i++) {
            if (!checkSteps.get(i) && firstNotPassedCheckPoint == -1) {
                firstNotPassedCheckPoint = i;
            }
        }

        // if a checkpoint was missed
        if (firstNotPassedCheckPoint != -1 && checkSteps.cardinality() != firstNotPassedCheckPoint - 1) {
            return -10;
        }

        // returns a reward only when a new checkpoint is passed|
        if (checkSteps.cardinality() > checkedPoints) {
            checkedPoints++;
            return checkSteps.cardinality() * 2;
        } else {
            return 0;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateCheckpoints();
        // the trackRaceImage is updated in update

        // draws the image to the panel
        g.drawImage(trackRaceImage.getScaledInstance((int) (size * scale), (int) (size * scale), Image.SCALE_FAST), 0, 0, null);

        if (drawInfo) {
            long currentTime = System.currentTimeMillis() - startTime;
            String time = addZeroIfNeeded(currentTime / 1000) + ":" + addZeroIfNeeded((currentTime% 1000) / 10);
            System.out.print("\r[" + time + "] REWARD: " + getReward() + " - CAR:" + car.toString());
            g.setFont(INFO_FONT);
            g.setColor(Color.WHITE);
            g.drawString(car.toString(), 10, 10);
            g.drawString("CurReward: " + currentReward, 10, 25);
            g.drawString("TotReward: " + totalReward, 10, 40);
            g.drawString("Checks: " + checkSteps.toString(), 10, 55);
            g.drawString("TIME: " + time, 10, 70);
        }

        // adds info of reward
        g.setColor((currentReward < 0) ? Color.RED : Color.CYAN);
        g.fillRect((int) ((size * scale) - 8), (int) ((size * scale) - 8), 6, 6);
    }


    private void drawCar(Graphics2D imageGraphics) {

        // computes and draws the car
        double cx = car.getX();
        double cy = car.getY();
        imageGraphics.setStroke(carStrokeSize);

        if (car.isSimple()) {
            imageGraphics.drawLine((int) cx, (int) cy, (int) cx, (int) cy);
        } else {
            double carTailDirection = (car.getDirection() - 180) % 360;
            double carLength = Math.abs(size / 15);
            // double carLength = Math.abs(car.getVelocity().speed * 1.5);

            double cosAngle = Math.cos(Math.toRadians(carTailDirection)) * carLength;
            double sinAngle = Math.sin(Math.toRadians(carTailDirection)) * carLength;

            // draws the car body
            imageGraphics.setColor(CAR_BODY_COLOR);
            imageGraphics.drawLine((int) cx, (int) cy, (int) (cx + cosAngle * 1.01), (int) (cy + sinAngle * 1.01));

            // draws the car head
            imageGraphics.setColor(CAR_HEAD_COLOR);
            imageGraphics.drawLine((int) cx, (int) cy, (int) cx, (int) cy);
        }
    }

    private String addZeroIfNeeded(long value) {
        return value < 10 ? "0" + value : "" + value;
    }

    public byte[] getCurrentFrame() {
        WritableRaster raster = trackRaceImage.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    public BufferedImage getCurrentImage() {
        return trackRaceImage;
    }

    public boolean isCarOutsideScreen() {
        return car.getX() < 0 || car.getY() < 0 || car.getX() >= size || car.getY() >= size;
    }

    public boolean isCarOnTrack() {
        if (isCarOutsideScreen()) {
            return false;
        }
        int pixel = trackImage.getRGB(car.getX(), car.getY());

        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return !(red == GRASS_COLOR.getRed() && green == GRASS_COLOR.getGreen() && blue == GRASS_COLOR.getBlue());
    }

    public void updateTrack() {

        // creates the image where to draw
        Graphics2D graphics = (Graphics2D) trackRaceImage.getGraphics();

        // draws the track and the car
        graphics.drawImage(trackImage, 0, 0, null);
        drawCar(graphics);

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

    public DrivingKeyListener getKeyListener() {
        return keyListener;
    }

    public void reset() {
        checkSteps = new BitSet(checkPoints.size());
        totalReward = 0;
        startTime = System.currentTimeMillis();
    }
}
