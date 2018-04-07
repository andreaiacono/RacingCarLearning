package me.andreaiacono.racinglearning.rl;

import com.fasterxml.jackson.databind.JsonNode;
import me.andreaiacono.racinglearning.core.Game;
import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning.QLConfiguration;

import java.io.File;

public class QLearning {

    private final Game game;

//    FIX SCRREEN SIZE
    public static HistoryProcessor.Configuration RACING_HP = new HistoryProcessor.Configuration(
            10,       //History length
            150,     //resize width
            109,     //resize height
            20,      //crop width
            20,      //crop height
            0,       //cropping x offset
            0,       //cropping y offset
            4        //skip mod (one frame is picked every x
    );

    public static QLConfiguration RACING_QL = new QLConfiguration(
                    12345,      //Random seed
                    10000,    //Max step By epoch
                    10000000,  //Max step
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
            0.5,    //learning rate
            0.000,              //l2 regularization
            null,
            null
    );


    public QLearning(Game game) {
        this.game = game;
    }

    public void learn(String model) throws Exception {

        DataManager manager = new DataManager(true);
        RacingMDP mdp = new RacingMDP(game);

        // setups and starts training
        QLearningDiscreteConv<ScreenFrameState> dql = new QLearningDiscreteConv(mdp, RACING_NET_CONFIG, RACING_HP, RACING_QL, manager);
        dql.train();

        dql.getNeuralNet().save(model);
        mdp.close();
    }


    public void race(String model) throws Exception {

        DataManager manager = new DataManager(false);

        RacingMDP mdp = new RacingMDP(game);
        QLearningDiscreteConv<ScreenFrameState> dql = new QLearningDiscreteConv(mdp, RACING_NET_CONFIG, RACING_HP, RACING_QL, manager);

        // loads the trained model
        manager.load(new File(model), JsonNode.class);

        // runs the model
        dql.getPolicy().play(mdp);

        mdp.close();
    }


}
