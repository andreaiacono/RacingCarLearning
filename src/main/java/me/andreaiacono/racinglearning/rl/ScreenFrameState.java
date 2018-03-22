package me.andreaiacono.racinglearning.rl;

import org.deeplearning4j.rl4j.space.Encodable;

/**
 * This is a frame of the screen with the circuit and the car.
 */
public class ScreenFrameState implements Encodable {

    double[] array;

    public ScreenFrameState(byte[] screen) {
        array = new double[screen.length];
        for (int i = 0; i < screen.length; i++) {
            array[i] = (screen[i] & 0xFF) / 255.0;
        }
    }

    @Override
    public double[] toArray() {
        return array;
    }
}
