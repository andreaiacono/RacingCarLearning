package me.andreaiacono.racinglearning.core;


import java.util.Arrays;

/**
 * The car can move in two directions: frontal and lateral. All the combinations of the two are all
 * the possible moves of the car. Each of the two directions can have three values [-1, 0 1].
 */
public enum Command {
    ACCELERATE(1, 0),
    BRAKE(-1, 0),
    TURN_LEFT(0, -1),
    TURN_RIGHT(0, 1),
    ACCELERATE_TURN_LEFT(1, -1),
    ACCELERATE_TURN_RIGHT(1, 1),
    BRAKE_TURN_LEFT(-1, -1),
    BRAKE_TURN_RIGHT(-1, 1),
    NO_OP(0, 0);

    private int frontal;
    private int lateral;

    Command(int frontal, int lateral) {
        this.frontal = frontal;
        this.lateral = lateral;
    }

    public static Command getCommand(int frontal, int lateral) {
        return Arrays.stream(Command.values()).filter(c -> c.frontal == frontal && c.lateral == lateral).findFirst().get();
    }

    public int getFrontalValue() {
        return frontal;
    }

    public int getLateralValue() {
        return lateral;
    }
}

