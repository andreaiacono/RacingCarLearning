package me.andreaiacono.racinglearning.game;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import me.andreaiacono.racinglearning.core.Command;
import me.andreaiacono.racinglearning.core.Game;
import me.andreaiacono.racinglearning.rl.ActionSpace;
import me.andreaiacono.racinglearning.rl.LruReplayBuffer;
import me.andreaiacono.racinglearning.rl.ReplayBuffer;
import me.andreaiacono.racinglearning.rl.agent.RlAgent;
import me.andreaiacono.racinglearning.rl.env.RlEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import static me.andreaiacono.racinglearning.ai.NetworkTrainer.OBSERVE_STEPS;
import static me.andreaiacono.racinglearning.game.TrainingState.EXPLORE;
import static me.andreaiacono.racinglearning.game.TrainingState.OBSERVE;

public class CarEnvironment implements RlEnv {

    private static final Logger logger = LoggerFactory.getLogger(CarEnvironment.class);

    private final NDManager manager;
    private final ReplayBuffer replayBuffer;
    private final Queue<NDArray> imgQueue = new ArrayDeque<>(4);
    private BufferedImage currentImg;
    private NDList currentObservation;
    private ActionSpace carActionSpace;
    private Game game;

    public static int gameStep = 0;
    public static int trainStep = 0;
    public TrainingState trainingState = OBSERVE;

    /**
     * Constructs a {@link CarEnvironment} with a basic {@link LruReplayBuffer}.
     *  @param manager          the manager for creating the game in
     * @param batchSize        the number of steps to train on per batch
     * @param replayBufferSize the number of steps to hold in the buffer
     * @param game
     */
    public CarEnvironment(NDManager manager, int batchSize, int replayBufferSize, Game game) {
        this(manager, new LruReplayBuffer(batchSize, replayBufferSize));
        this.game = game;
        carActionSpace = new ActionSpace();
        currentImg = this.game.getCurrentImage();
        currentObservation = createObservation(currentImg);
    }

    /**
     * Constructs a {@link CarEnvironment}.
     *
     * @param manager      the manager for creating the game in
     * @param replayBuffer the replay buffer for storing data
     */
    public CarEnvironment(NDManager manager, ReplayBuffer replayBuffer) {
        this.manager = manager;
        this.replayBuffer = replayBuffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Step[] runEnvironment(RlAgent agent, boolean training) {
        Step[] batchSteps = new Step[0];
//        reset();

        // run the game
        NDList action = agent.chooseAction(this, training);
        step(action, training);
        if (training) {
            batchSteps = this.getBatch();
        }
        if (gameStep % 5000 == 0) {
            this.closeStep();
        }
        trainingState = gameStep <= OBSERVE_STEPS ? OBSERVE : EXPLORE;
        gameStep++;
        return batchSteps;
    }

    /**
     * {@inheritDoc}
     * action[0] == 1 : do nothing
     * action[1] == 1 : flap the bird
     *
     * @return
     */
    @Override
    public void step(NDList action, boolean training) {
        float currentReward = game.move(Command.fromAction(action));

        NDList preObservation = currentObservation;
        currentObservation = createObservation(game.getCurrentImage());

        CarEnvironmentStep step = new CarEnvironmentStep(manager.newSubManager(),
                preObservation, currentObservation, action, currentReward, game.isOver());
        if (training) {
            replayBuffer.addStep(step);
        }

        if (gameStep % 100 == 0) {
            System.out.print("\rGAME_STEP " + gameStep +
                    " / " + "TRAIN_STEP " + trainStep +
                    " / " + trainingState +
                    " / " + "ACTION " + (Arrays.toString(action.singletonOrThrow().toArray())) +
                    " / " + "REWARD " + step.getReward().getFloat());
        }
        if (game.isOver()) {
            restartGame();
        }
    }

    @Override
    public NDList getObservation() {
        return currentObservation;
    }

    @Override
    public ActionSpace getActionSpace() {
        return this.carActionSpace;
    }

    @Override
    public Step[] getBatch() {
        return replayBuffer.getBatch();
    }

    public void closeStep() {
        replayBuffer.closeStep();
    }

    @Override
    public void close() {
        manager.close();
    }

    @Override
    public void reset() {
        game.reset();
    }


    /**
     * Convert image to CNN input.
     * Copy the initial frame image, stack into NDList,
     * then replace the fourth frame with the current frame to ensure that the batch picture is continuous.
     *
     * @param currentImg the image of current frame
     * @return the CNN input
     */
    public NDList createObservation(BufferedImage currentImg) {
        NDArray observation = NDImageUtils.toTensor(
            ImageFactory.getInstance().fromImage(currentImg).toNDArray(NDManager.newBaseManager(), Image.Flag.GRAYSCALE)
        );

        if (imgQueue.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                imgQueue.offer(observation);
            }
            return new NDList(NDArrays.stack(new NDList(observation, observation, observation, observation), 1));
        } else {
            imgQueue.remove();
            imgQueue.offer(observation);
            NDArray[] buf = new NDArray[4];
            int i = 0;
            for (NDArray nd : imgQueue) {
                buf[i++] = nd;
            }
            return new NDList(NDArrays.stack(new NDList(buf[0], buf[1], buf[2], buf[3]), 1));
        }
    }

    private void restartGame() {
        game.reset();
    }
}
