package me.andreaiacono.racinglearning.rl;

import me.andreaiacono.racinglearning.core.Game;
import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscrete;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.util.DataManager;

public class QLearning {

    private final Game game;

    public static HistoryProcessor.Configuration RACING_HP =
            new HistoryProcessor.Configuration(
                    4,       //History length
                    100,      //resize width
                    25,     //resize height
                    84,      //crop width
                    84,      //crop height
                    0,       //cropping x offset
                    0,       //cropping y offset
                    4        //skip mod (one frame is picked every x
            );

    public static org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning.QLConfiguration RACING_QL =
            new org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning.QLConfiguration(
                    123,      //Random seed
                    10000,    //Max step By epoch
                    8000000,  //Max step
                    1000000,  //Max size of experience replay
                    32,       //size of batches
                    10000,    //target update (hard)
                    500,      //num step noop warmup
                    0.1,      //reward scaling
                    0.99,     //gamma
                    100.0,    //td-error clipping
                    0.1f,     //min epsilon
                    100000,   //num step for eps greedy anneal
                    true      //double-dqn
            );

    public static DQNFactoryStdConv.Configuration RACING_NET_CONFIG =
            new DQNFactoryStdConv.Configuration(
                    0.00025, //learning rate
                    0.000,   //l2 regularization
                    null, null
            );


    public QLearning(Game game) {
        this.game = game;
    }

    public void startLearning() {

        try {

            DataManager manager = new DataManager(true);
            System.out.println("started data manager");

            //setup the emulation environment through ALE, you will need a ROM file
            RacingMDP mdp = new RacingMDP(game);
            System.out.println("started mdp");

            //setup the training
            QLearningDiscreteConv<ScreenFrameState> dql = new QLearningDiscreteConv(mdp, RACING_NET_CONFIG, RACING_HP, RACING_QL, manager);
            System.out.println("started dql");

            //start the training
            dql.train();

            //save the model at the end
            dql.getPolicy().save("racing-dql.model");

            //close the ALE env
            mdp.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * the image computed by the algorithm is the difference between the current frame
     * and the preceding frame; in this way we can give the algorithm an idea of the
     * movement of the car inside the circuit
     *
     * @param frame1
     * @param frame2
     * @return
     */
    private byte[] computeDelta(byte[] frame1, byte[] frame2) {
        byte[] delta = new byte[frame1.length];
        for (int i = 0; i < delta.length; i++) {
            delta[i] = (byte) (frame2[i] - frame1[i]);
        }
        return delta;
    }
}
