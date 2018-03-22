package me.andreaiacono.racinglearning.rl;

import me.andreaiacono.racinglearning.core.Command;
import org.deeplearning4j.rl4j.space.ActionSpace;

import java.util.Random;

public class CarActionSpace implements ActionSpace<Integer> {

    private Random rd;
    final private int size;

    public CarActionSpace() {
        size = Command.values().length;
        rd = new Random();
    }

    @Override
    public Integer randomAction() {
        return rd.nextInt(size);
    }

    @Override
    public void setSeed(int seed) {
        rd = new Random(seed);
    }

    @Override
    public Object encode(Integer value) {
        return value;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Integer noOp() {
        // the NOOP is the last command of the Command enum
        return Command.values().length-1;
    }

}
