package me.andreaiacono.racinglearning.game;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import me.andreaiacono.racinglearning.rl.env.RlEnv;

class CarEnvironmentStep implements RlEnv.Step {

    private final NDManager manager;
    private final NDList preObservation;
    private final NDList postObservation;
    private final NDList action;
    private final float reward;
    private final boolean terminal;


    public CarEnvironmentStep(NDManager manager, NDList preObservation, NDList postObservation, NDList action, float reward, boolean terminal) {
        this.manager = manager;
        this.preObservation = preObservation;
        this.postObservation = postObservation;
        this.action = action;
        this.reward = reward;
        this.terminal = terminal;
    }

    @Override
    public NDList getPreObservation(NDManager manager) {
        preObservation.attach(manager);
        return preObservation;
    }

    @Override
    public NDList getPreObservation() {
        return preObservation;
    }

    @Override
    public NDList getPostObservation(NDManager manager) {
        postObservation.attach(manager);
        return postObservation;
    }

    @Override
    public NDList getPostObservation() {
        return postObservation;
    }


    @Override
    public NDManager getManager() {
        return this.manager;
    }

    @Override
    public NDList getAction() {
        return action;
    }

    @Override
    public NDArray getReward() {
        return manager.create(reward);
    }

    @Override
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public void close() {
        this.manager.close();
    }
}

