package me.andreaiacono.racinglearning.rl;

import me.andreaiacono.racinglearning.core.Command;
import org.deeplearning4j.rl4j.space.DiscreteSpace;

public class CarActionSpace extends DiscreteSpace {

    public CarActionSpace() {
        super(Command.values().length);
    }

    @Override
    public Integer noOp() {
        // the NOOP is the last command of the Command enum
        return size - 1;
    }
}
