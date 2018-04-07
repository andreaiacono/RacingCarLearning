package me.andreaiacono.racinglearning.gui;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.misc.DrivingKeyListener;
import me.andreaiacono.racinglearning.core.Car;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.text.SimpleDateFormat;
import java.util.BitSet;

public class TrackPanel extends JPanel {

    private static final int SMALL_CAR_LENGTH = 3;
    private static final int SMALL_CAR_WIDTH = 8;
    private static final int BIG_CAR_LENGTH = SMALL_CAR_LENGTH * 2;
    private static final int BIG_CAR_WIDTH = SMALL_CAR_WIDTH * 2;
    private static final Color CAR_COLOR = Color.RED;
    private static final Font INFO_FONT = new Font("Arial", Font.PLAIN, 10);
    private static final Point SMALL_CAR_STARTING_POSITION = new Point(102, 12);
    private static final Point BIG_CAR_STARTING_POSITION = new Point(204, 25);
    public static final int CAR_STARTING_ANGLE = 0;

    private int IMAGE_WIDTH;
    private int IMAGE_HEIGHT;

    // the points of the tracks that car has to pass into in order
    // to consider a valid lap (otherwise it could get shortcuts)
    private Rectangle[] bigCheckPoints = {
            new Rectangle(214, 11, 10, 30),
            new Rectangle(262, 70, 30, 10),
            new Rectangle(120, 175, 10, 30),
            new Rectangle(14, 142, 30, 10),
            new Rectangle(65, 100, 10, 30),
            new Rectangle(180, 80, 35, 10),
            new Rectangle(92, 62, 30, 10),
    };
    private Rectangle[] smallCheckPoints = {
            new Rectangle(107, 5, 5, 15),
            new Rectangle(131, 35, 15, 5),
            new Rectangle(60, 87, 5, 15),
            new Rectangle(7, 71, 15, 5),
            new Rectangle(32, 50, 5, 15),
            new Rectangle(90, 40, 17, 5),
            new Rectangle(46, 31, 15, 5),
    };

    private Rectangle[] checkPoints;


    // the time limits for each checkpoint
    private long[] smallCheckPointMaxTimes = {1, 2, 4, 6, 7, 8, 10, 12};
    private long[] checkPointMaxTimes = {2, 5, 9, 12, 14, 17, 21, 25};

    private BitSet checkSteps;

    private static final String CIRCUIT_FILENAME = "track.png";
    private static final String REWARD_CIRCUIT_FILENAME = "track_reward.png";
    private static final String SMALL_CIRCUIT_FILENAME = "track_small.png";
    private static final String SMALL_REWARD_CIRCUIT_FILENAME = "track_reward_small.png";
    private boolean drawInfo;
    private final BufferedImage circuitImage;
    private final BufferedImage rewardCircuitImage;
    private final Car car;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    private BufferedImage bufferedImage;
    private long time;
    private boolean isLapCompleted = false;
    private Stroke thickStroke = new BasicStroke(2.5f);
    private long startTime;
    private double carWidth;
    private double carLength;

    public TrackPanel(Car car, DrivingKeyListener listener, GameParameters gameParameters) throws Exception {
        this.drawInfo = gameParameters.getBool(GameParameters.DRAW_INFO_PARAM);
        String circuitFilename = gameParameters.getBool(GameParameters.SMALL_PARAM) ? SMALL_CIRCUIT_FILENAME : CIRCUIT_FILENAME;
        String rewardCircuitFilename = gameParameters.getBool(GameParameters.SMALL_PARAM) ? SMALL_REWARD_CIRCUIT_FILENAME : REWARD_CIRCUIT_FILENAME;

        circuitImage = gameParameters.getBool(GameParameters.USE_BW_PARAM)
                ? ImageIO.read(ClassLoader.getSystemResource(rewardCircuitFilename))
                : ImageIO.read(ClassLoader.getSystemResource(circuitFilename));

        rewardCircuitImage = ImageIO.read(ClassLoader.getSystemResource(rewardCircuitFilename));

        checkPoints = gameParameters.getBool(GameParameters.SMALL_PARAM) ? smallCheckPoints : bigCheckPoints;
        bufferedImage = new BufferedImage(circuitImage.getWidth(), circuitImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        IMAGE_WIDTH = circuitImage.getWidth();
        IMAGE_HEIGHT = circuitImage.getHeight();

        this.car = car;
        startTime = System.currentTimeMillis();
        setFocusable(true);
        addKeyListener(listener);
        checkSteps = new BitSet(checkPoints.length);

        Point startingPosition = gameParameters.getBool(GameParameters.SMALL_PARAM) ? SMALL_CAR_STARTING_POSITION : BIG_CAR_STARTING_POSITION;
        car.setStartingPosition(startingPosition);
        car.setMaxSpeed(gameParameters.getBool(GameParameters.SMALL_PARAM) ? Car.SMALL_MAX_SPEED : Car.BIG_MAX_SPEED);
        car.reset();

        carWidth = gameParameters.getBool(GameParameters.SMALL_PARAM) ? SMALL_CAR_WIDTH : BIG_CAR_WIDTH;
        carLength = gameParameters.getBool(GameParameters.SMALL_PARAM) ? SMALL_CAR_LENGTH : BIG_CAR_LENGTH;
    }

    /**
     * Computes the rewards associated to the position of the car in the track.
     * If the car is on the track, the reward will be high; if the car is outside the
     * track, the reward will be low. Also, the more checkpoints passed, the better.
     *
     * @return the reward for the current position of the car (based on the path it took to arrive there)
     */
    public int getReward() {

        if (!isCarInsideScreen()) {
            return -100000;
        }

        // the faster the car goes, the better
        int reward = (int) car.getVelocity().speed * 10;

        // the more checkpoints passed in order, the more reward gained
        reward += getCheckPointsReward();

        // being on track is better than being off track
        reward += isCarOnTrack() ? 1000 : -1000;

//        // the more time passes, the worse is  /// MISLEADING!
//        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
//        reward -= 20 * elapsedTime;

        // if the lap was completed, super reward!
        if (isLapCompleted) {
            reward += 100000;
        }

        return reward;
    }

    // the checkpoints give a positive reward only if they're passed in sequence
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
            return -5000;
        }

        // returns a progressive positive reward
        return checkSteps.cardinality() * 50;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateCheckpoints();

        // creates the image where to draw
        Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();

        // draws the track and the car
        imageGraphics.drawImage(circuitImage, 0, 0, null);
        drawCar(imageGraphics);

        // draws info
        if (drawInfo) {
            String time = addZeroIfNeeded(this.time / 1000) + ":" + addZeroIfNeeded((this.time % 1000) / 10);
            imageGraphics.setFont(INFO_FONT);
            imageGraphics.drawString(car.toString(), 10, 10);
            imageGraphics.drawString("REWARD: " + getReward(), 10, 20);
            imageGraphics.drawString("Checks: " + checkSteps.toString(), 10, 30);
            imageGraphics.drawString("TIME: " + time, 10, 40);
        }

        // draws the image to the panel
        g.drawImage(bufferedImage, 0, 0, null);
    }

    public void drawCar(Graphics2D imageGraphics) {

        // computes and draws the car
        double cx = car.getX();
        double cy = car.getY();
        double angleLeft = car.getDirection() - carWidth;
        double angleRight = car.getDirection() + carWidth;

        double cosAngleLeft = Math.cos(Math.toRadians(angleLeft)) * carLength;
        double sinAngleLeft = Math.sin(Math.toRadians(angleLeft)) * carLength;
        double cosAngleRight = Math.cos(Math.toRadians(angleRight)) * carLength;
        double sinAngleRight = Math.sin(Math.toRadians(angleRight)) * carLength;
        double cosAngleBackLeft = Math.cos(Math.toRadians((angleLeft - 180) % 360)) * carLength;
        double sinAngleBackLeft = Math.sin(Math.toRadians((angleLeft - 180) % 360)) * carLength;
        double cosAngleBackRight = Math.cos(Math.toRadians((angleRight - 180) % 360)) * carLength;
        double sinAngleBackRight = Math.sin(Math.toRadians((angleRight - 180) % 360)) * carLength;

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

    public byte[] getCurrentFrame() {
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    public boolean isCarInsideScreen() {
        return car.getX() >= 0 && car.getY() >= 0 && car.getX() < IMAGE_WIDTH && car.getY() < IMAGE_HEIGHT;
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
        startTime = System.currentTimeMillis();
        checkSteps.clear();
    }

    public int getScreenHeight() {
        return bufferedImage.getHeight();
    }

    public int getScreenWidth() {
        return bufferedImage.getWidth();
    }

    /**
     * checks that every check point is reached within its time limit.
     *
     * @return
     */
    public boolean isTimeOver() {

        long currentTime = System.currentTimeMillis();

        int firstNotPassedCheck = 0;
        while (firstNotPassedCheck < checkSteps.length()) {
            if (!checkSteps.get(firstNotPassedCheck)) {
                break;
            }
            firstNotPassedCheck++;
        }

        long elapsedTime = (currentTime - startTime) / 1000;
        return elapsedTime > checkPointMaxTimes[firstNotPassedCheck];
    }

//    /**
//     * the image computed by the algorithm is the difference between the current frame
//     * and the preceding frame; in this way we can give the algorithm an idea of the
//     * movement of the car inside the track
//     *
//     * @param frame1
//     * @param frame2
//     * @return
//     */
//    private byte[] computeDelta(byte[] frame1, byte[] frame2) {
//        byte[] delta = new byte[frame1.length];
//        for (int i = 0; i < delta.length; i++) {
//            delta[i] = (byte) (frame2[i] - frame1[i]);
//        }
//        return delta;
//    }
}
