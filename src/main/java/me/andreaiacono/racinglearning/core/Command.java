package me.andreaiacono.racinglearning.core;

import ai.djl.ndarray.NDList;

import java.util.Arrays;

/**
 * The car can move in two directions: frontal and lateral. All the combinations of the two are all
 * the possible moves of the car. Each of the two directions can have three values [-1, 0 1].
 */
public enum Command {

    ACCELERATE(new int[]{1, 0}),
//    BRAKE(new int[]{-1, 0}),
    TURN_LEFT(new int[]{0, -1}),
    TURN_RIGHT(new int[]{0, 1}),
//    ACCEL_LEFT(new int[]{1, -1}),
//    ACC_RIGHT(new int[]{1, 1}),
//    BRAKE_LEFT(new int[]{-1, -1}),
//    BRAKE_RIGHT(new int[]{-1, 1}),
    NO_OP(new int[]{0, 0});

    public final int[] directions;

    Command(int[] directions) {
        this.directions = directions;
    }

    public static Command getCommand(int frontal, int lateral) {
        return Arrays
                .stream(Command.values())
                .filter(c -> c.directions[0] == frontal && c.directions[1] == lateral)
                .findFirst()
                .orElse(NO_OP);
    }

    public static Command fromAction(NDList action) {
        int[] values = action.get(0).toIntArray();
        for (int i=0; i<values.length; i++) {
            if (values[i] != 0) {
                return Command.values()[i];
            }
        }
        return NO_OP;
    }

    public int getFrontalValue() {
        return directions[0];
    }

    public int getLateralValue() {
        return directions[1];
    }
}

