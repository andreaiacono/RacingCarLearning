package me.andreaiacono.racinglearning.rl;

import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.core.GameParameters;
import org.apache.commons.io.IOUtils;
import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.util.DataManager;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

import static me.andreaiacono.racinglearning.misc.Constants.SCREEN_SIZE;

public class RacingQL {

    public static final int MAX_MOVES_PER_EPOCH = 500;
    private final Game game;

//    private static HistoryProcessor.Configuration HP_CONF = new HistoryProcessor.Configuration(
//            4,             //History length
//            SCREEN_SIZE,    //resize width
//            SCREEN_SIZE,    //resize height
//            SCREEN_SIZE,    //crop width
//            SCREEN_SIZE,    //crop height
//            0,              //cropping x offset
//            0,              //cropping y offset
//            4              //skip mod (one frame is picked every x
//    );

    private static HistoryProcessor.Configuration HP_CONF = HistoryProcessor.Configuration.builder()
            .historyLength(4)
            .rescaledWidth(SCREEN_SIZE)
            .rescaledHeight(SCREEN_SIZE)
            .croppingWidth(SCREEN_SIZE)
            .croppingHeight(SCREEN_SIZE)
            .offsetX(0)
            .offsetY(0)
            .skipFrame(4)
            .build();

//    private static QLConfiguration QL_CONF = new QLConfiguration(
//                    123,      //Random seed
//                    MAX_MOVES_PER_EPOCH,    //Max step By epoch
//                    1000000,  //Max step
//                    1000000,  //Max size of experience replay
//                    32,       //size of batches
//                    10000,    //target update (hard)
//                    500,      //num step noop warmup
//                    0.0001,      //reward scaling
//                    0.0001,      //gamma
//                    100.0,    //td-error clipping
//                    0.1f,     //min epsilon
//                    100000,   //num step for eps greedy anneal
//                    true      //double-dqn
//            );

    QLearning.QLConfiguration QL_CONF = QLearning.QLConfiguration.builder()
            .seed(123)
            .maxEpochStep(MAX_MOVES_PER_EPOCH)
            .maxStep(1_900_000)
            .expRepMaxSize(1_000_000)
            .batchSize(32)
            .targetDqnUpdateFreq(10000)
            .updateStart(500)
            .rewardFactor(0.01)
            .gamma(0.5)
            .errorClamp(100.0)
            .minEpsilon(0.1f)
            .epsilonNbStep(100000)
            .doubleDQN(true)
            .build();

//     private static NetworkConfiguration NET_CONF = NetworkConfiguration.builder()
//     .learningRate(0.9)
//     .l2(0.0) // l2 regularization
//     .build();


    private static DQNFactoryStdConv.Configuration RACING_NET_CONFIG = new DQNFactoryStdConv.Configuration(
            0.01,    //learning rate
            0.000,              //l2 regularization
            null,
            null
    );


    public RacingQL(Game game) {
        this.game = game;
    }

    public void learn(String model) throws Exception {

        DataManager manager = new DataManager(true);
        RacingMDP mdp = new RacingMDP(game);

        Process p = Runtime.getRuntime().exec("hostname");
        p.waitFor();
        String hostName = IOUtils.toString(p.getInputStream(), StandardCharsets.UTF_8).trim();
        String filename = getNewFilename(model);
        saveRunningConfig(filename + ".config", hostName, 0, 0, "");

        // setups and starts training
        QLearningDiscreteConv<ScreenFrameState> dql = new QLearningDiscreteConv(mdp, RACING_NET_CONFIG, HP_CONF, QL_CONF);
        long start = System.currentTimeMillis();

        dql.train();

        long elapsed = System.currentTimeMillis() - start;
        int seconds = (int) (elapsed / 1000) % 60;
        int minutes = (int) ((elapsed / (1000 * 60)) % 60);
        int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);
        int days = (int) ((elapsed / (1000 * 60 * 60 * 24)));
        String elapsedTime = String.format("%dd:%02dh:%02dm:%02ds", days, hours, minutes, seconds);

        dql.getPolicy().save(filename + ".model");
        mdp.close();

        game.saveChartImage(filename + ".png");
        saveRunningConfig(filename + ".config", hostName, dql.getEpochCount(), start, elapsedTime);
    }

    private void saveRunningConfig(String filename, String hostName, int epochCounter, long start, String elapsedTime) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            writer.append("RL4J Configuration values\n");

            if (epochCounter > 0) {
                writer.append("\nOperating System: ").append(System.getProperty("os.name")).append(" [").append(hostName).append("]");
                writer.append("\nStart Time: ").append(new Date(start).toString());
                writer.append("\nElapsed Time: ").append(elapsedTime);
                writer.append("\nEpochs: ").append(String.valueOf(epochCounter));
                writer.append("\nScreen size (in pixel): ").append(String.valueOf(game.track.getSizeInPixel()));
            }
            writer.append("\n\nHISTORY PROCESSOR:\n");
            writer.append("\tHistory Length: ").append(String.valueOf(HP_CONF.getHistoryLength())).append("\n");
            writer.append("\tResize Width: ").append(String.valueOf(HP_CONF.getRescaledWidth())).append("\n");
            writer.append("\tResize Height: ").append(String.valueOf(HP_CONF.getRescaledHeight())).append("\n");
            writer.append("\tCrop Width: ").append(String.valueOf(HP_CONF.getCroppingWidth())).append("\n");
            writer.append("\tCrop Height: ").append(String.valueOf(HP_CONF.getCroppingHeight())).append("\n");
            writer.append("\tOffset X: ").append(String.valueOf(HP_CONF.getOffsetX())).append("\n");
            writer.append("\tOffset Y: ").append(String.valueOf(HP_CONF.getOffsetY())).append("\n");
            writer.append("\tSkip Frame: ").append(String.valueOf(HP_CONF.getSkipFrame())).append("\n");
            if (game.getGameParams().isProvided(GameParameters.EASY_PARAM)) {
                writer.append("\tEasy Mode: ").append("" + game.getGameParams().getFloat(GameParameters.EASY_PARAM, 0.5f)).append("\n");
            }

            writer.append("\nQL CONFIGURATION:\n");
            writer.append("\tRandom seed: ").append(String.valueOf(QL_CONF.getSeed())).append("\n");
            writer.append("\tMax Step by Epoch: ").append(String.valueOf(QL_CONF.getMaxEpochStep())).append("\n");
            writer.append("\tMax Step: ").append(String.valueOf(QL_CONF.getMaxStep())).append("\n");
            writer.append("\tMax size of experience replay: ").append(String.valueOf(QL_CONF.getExpRepMaxSize())).append("\n");
            writer.append("\tSize of batches: ").append(String.valueOf(QL_CONF.getBatchSize())).append("\n");
            writer.append("\tTarget Update: ").append(String.valueOf(QL_CONF.getTargetDqnUpdateFreq())).append("\n");
            writer.append("\tNum Step No-op Warmup: ").append(String.valueOf(QL_CONF.getUpdateStart())).append("\n");
            writer.append("\tReward Scaling: ").append(String.valueOf(QL_CONF.getRewardFactor())).append("\n");
            writer.append("\tGamma: ").append(String.valueOf(QL_CONF.getGamma())).append("\n");
            writer.append("\tTD-Error Clipping: ").append(String.valueOf(QL_CONF.getErrorClamp())).append("\n");
            writer.append("\tMin Epsilon: ").append(String.valueOf(QL_CONF.getMinEpsilon())).append("\n");
            writer.append("\tNum Steps for EPS greedy anneal: ").append(String.valueOf(QL_CONF.getEpsilonNbStep())).append("\n");
            writer.append("\tDouble DQN: ").append(String.valueOf(QL_CONF.isDoubleDQN())).append("\n");

            writer.append("\nNET CONFIGURATION:\n");
            writer.append("\tLearning Rate: ").append(String.valueOf(RACING_NET_CONFIG.getLearningRate())).append("\n");
            writer.append("\tL2 Regularization: ").append(String.valueOf(RACING_NET_CONFIG.getL2())).append("\n");

            if (epochCounter == 0) {
                writer.append("\nEXECUTION NOT TERMINATED\n");
            }

        }
    }

    private String getNewFilename(String basename) {
        long index = 10;
        String directory = "src/main/resources/models/deeplearning4java/";
        String filename;
        while (true) {
            filename = String.format("%s%s_%d", directory, basename, index);
            if (!new File(filename + ".config").exists()) {
                break;
            }
            index++;
        }
        return filename;
    }


    public void race(String model) throws Exception {

        RacingMDP mdp2 = new RacingMDP(game);
        DQNPolicy<ScreenFrameState> pol2 = DQNPolicy.load(model + ".policy");

        //evaluate the agent
        double rewards = 0;
        for (int i = 0; i < 1000; i++) {
            mdp2.reset();
            double reward = pol2.play(mdp2);
            rewards += reward;
            Logger.getAnonymousLogger().info("Reward: " + reward);
        }

        mdp2.close();
        Logger.getAnonymousLogger().info("average: " + rewards / 1000);

//
////        DataManager manager = new DataManager(true);
//        RacingMDP mdp = new RacingMDP(game);
////        QLearningDiscreteConv<ScreenFrameState> dql = new QLearningDiscreteConv(mdp, , RACING_HP, RACING_QL, manager);
//
//
//        DQN l = DQN.load(model);
//        TrackPanel circuit = game.track;
//        Car car = game.car;
//        mdp.reset();
//
//        while (!circuit.isLapCompleted()) {
//
//            long startTime = System.currentTimeMillis();
//
//            // gets the command from the model
//            Integer action = l.output()
//            mdp.step(action);
//
//            if (!circuit.isCarInsideScreen()) {
//                break;
//            }
//
//            // updates the position of the car
//            car.setIsOnTrack(circuit.isCarOnTrack());
//            car.updatePosition();
//
//            // refreshes the screen with the new position
//            circuit.updateTrack();
//
//            // wait for the next screen updatePosition
//            long elapsedTime = System.currentTimeMillis() - startTime;
//            long remainingTimeToScreenUpdate = Math.max(0, SCREEN_UPDATE - elapsedTime);
//            Thread.sleep(remainingTimeToScreenUpdate);
//        }

//        mdp.close();
    }


}
