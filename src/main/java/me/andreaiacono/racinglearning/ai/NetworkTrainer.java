package me.andreaiacono.racinglearning.ai;

import ai.djl.Model;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.Trainer;
import ai.djl.training.tracker.LinearTracker;
import ai.djl.training.tracker.Tracker;
import me.andreaiacono.racinglearning.game.CarEnvironment;
import me.andreaiacono.racinglearning.rl.agent.EpsilonGreedy;
import me.andreaiacono.racinglearning.rl.agent.QAgent;
import me.andreaiacono.racinglearning.rl.agent.RlAgent;
import me.andreaiacono.racinglearning.rl.env.RlEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static me.andreaiacono.racinglearning.ai.Config.setupTrainingConfig;
import static me.andreaiacono.racinglearning.misc.Constants.MODEL_PATH;

public final class NetworkTrainer {

    private static final Logger logger = LoggerFactory.getLogger(NetworkTrainer.class);

    public static final int OBSERVE_STEPS = 1000; // gameSteps to observe before training
    public static final int EXPLORE = 3000000; // frames over which to anneal epsilon
    public static final int SAVE_EVERY_STEPS = 100000; // save model every 100,000 step
    public static final int REPLAY_BUFFER_SIZE = 5000; // number of previous transitions to remember
    public static final float REWARD_DISCOUNT = 0.9f; // decay rate of past observations
    public static final float INITIAL_EPSILON = 0.09f;
    public static final float FINAL_EPSILON = 0.0001f;


    static RlEnv.Step[] batchSteps;

    public static void train(CarEnvironment carEnv, Model model, int screenSize, int batchSize) {
        boolean training = true;
        DefaultTrainingConfig config = setupTrainingConfig();
        try (Trainer trainer = model.newTrainer(config)) {
            trainer.initialize(new Shape(batchSize, 4, screenSize, screenSize));
            trainer.notifyListeners(listener -> listener.onTrainingBegin(trainer));

            RlAgent agent = new QAgent(trainer, REWARD_DISCOUNT);
            Tracker exploreRate =
                    LinearTracker.builder()
                            .setBaseValue(INITIAL_EPSILON)
                            .optSlope(-(INITIAL_EPSILON - FINAL_EPSILON) / EXPLORE)
                            .optMinValue(FINAL_EPSILON)
                            .build();
            agent = new EpsilonGreedy(agent, exploreRate);

            int numOfThreads = 8;
            List<Callable<Object>> callables = new ArrayList<>(numOfThreads);
            callables.add(new GeneratorCallable(carEnv, agent, training));
            if (training) {
                callables.add(new TrainerCallable(model, agent));
            }
            ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
            try {
                try {
                    List<Future<Object>> futures = new ArrayList<>();
                    for (Callable<Object> callable : callables) {
                        futures.add(executorService.submit(callable));
                    }
                    for (Future<Object> future : futures) {
                        future.get();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("", e);
                }
            } finally {
                executorService.shutdown();
            }
        }
    }

    public static void test(Model model, CarEnvironment carEnvironment) {
        DefaultTrainingConfig config = setupTrainingConfig();
        try (Trainer trainer = model.newTrainer(config)) {
            RlAgent agent = new QAgent(trainer, REWARD_DISCOUNT);
            while (true) {
                carEnvironment.runEnvironment(agent, false);
            }
        }
    }

    private static class TrainerCallable implements Callable<Object> {
        private final RlAgent agent;
        private final Model model;

        public TrainerCallable(Model model, RlAgent agent) {
            this.model = model;
            this.agent = agent;
        }

        @Override
        public Object call() throws Exception {
            while (CarEnvironment.trainStep < EXPLORE) {
                Thread.sleep(0);
                if (CarEnvironment.gameStep > OBSERVE_STEPS) {
                    this.agent.trainBatch(batchSteps);
                    CarEnvironment.trainStep++;
                    if (CarEnvironment.trainStep > 0 && CarEnvironment.trainStep % SAVE_EVERY_STEPS == 0) {
                        model.save(Paths.get(MODEL_PATH), "dqn-" + CarEnvironment.trainStep);
                    }
                }
            }
            return null;
        }
    }

    private static class GeneratorCallable implements Callable<Object> {
        private final CarEnvironment game;
        private final RlAgent agent;
        private final boolean training;

        public GeneratorCallable(CarEnvironment game, RlAgent agent, boolean training) {
            this.game = game;
            this.agent = agent;
            this.training = training;
        }

        @Override
        public Object call() {
            while (CarEnvironment.trainStep < EXPLORE) {
                batchSteps = game.runEnvironment(agent, training);
            }
            return null;
        }
    }


}
