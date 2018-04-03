package me.andreaiacono.racinglearning.rl;

import org.deeplearning4j.rl4j.space.Encodable;

/**
 * This is a frame of the screen with the track and the car.
 */
public class ScreenFrameState implements Encodable {

    double[] array;

    public ScreenFrameState(byte[] screen) {
        array = new double[screen.length];
        for (int i = 0; i < screen.length; i++) {
            // transforms the RGB byte value [0,255] into a double [0,1]
            array[i] = (screen[i] & 0xFF) / 255.0;
        }
    }

    @Override
    public double[] toArray() {
        return array;
    }
}
