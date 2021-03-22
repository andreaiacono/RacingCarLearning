package me.andreaiacono.racinglearning.rl;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * This is a frame of the screen with the track and the car.
 */
public class ScreenFrameState implements Encodable {

    final INDArray data;

    public ScreenFrameState(int[] shape, byte[] screen) {
        data = Nd4j.create(screen, new long[] {shape[1], shape[2], 3}, DataType.UBYTE).permute(2,0,1);
    }

    private ScreenFrameState(INDArray toDup) {
        data = toDup.dup();
    }

    @Override
    public double[] toArray() {
        return data.data().asDouble();
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return data;
    }

    @Override
    public ScreenFrameState dup() {
        return new ScreenFrameState(data);
    }
}
