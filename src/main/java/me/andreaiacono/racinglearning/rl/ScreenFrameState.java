package me.andreaiacono.racinglearning.rl;

import com.google.flatbuffers.FlatBufferBuilder;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.BaseNDArray;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.LongShapeDescriptor;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.nativeblas.Nd4jCuda;

/**
 * This is a frame of the screen with the track and the car.
 */
public class ScreenFrameState implements Encodable {

    double[] array;
    byte[] screen;

    public ScreenFrameState(byte[] screen) {
        this.screen = screen;
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

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return Nd4j.create(array);
    }

    @Override
    public Encodable dup() {
        return new ScreenFrameState(screen);
    }
}
