package me.andreaiacono.racinglearning.rl;

import me.andreaiacono.racinglearning.core.Game;
import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning.QLConfiguration;

public class QLearning {

    private final Game game;

    public static HistoryProcessor.Configuration RACING_HP = new HistoryProcessor.Configuration(
                    4,       //History length
                    299,     //resize width
                    218,     //resize height
                    25,      //crop width
                    25,      //crop height
                    0,       //cropping x offset
                    0,       //cropping y offset
                    4        //skip mod (one frame is picked every x
            );

    public static QLConfiguration RACING_QL = new QLConfiguration(
                    12345,      //Random seed
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

    public static DQNFactoryStdConv.Configuration RACING_NET_CONFIG = new DQNFactoryStdConv.Configuration(
                    0.00025,    //learning rate
                    0.000,              //l2 regularization
                    null,
                    null
            );


    public QLearning(Game game) {
        this.game = game;
    }

    public void startLearning() {

        try {

            DataManager manager = new DataManager(true);

            //setup the emulation environment through ALE, you will need a ROM file
            RacingMDP mdp = new RacingMDP(game);

            //setup the training
            QLearningDiscreteConv<ScreenFrameState> dql = new QLearningDiscreteConv(mdp, RACING_NET_CONFIG, RACING_HP, RACING_QL, manager);

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


}