package me.andreaiacono.racinglearning.ai;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import me.andreaiacono.racinglearning.core.Command;

import java.io.IOException;
import java.nio.file.Paths;

import static me.andreaiacono.racinglearning.misc.Constants.MODEL_PATH;

public class NetworkModel {

    public static final String PARAMS_PREFIX = "dqn-trained";

    public static Model createOrLoadModel(boolean usePretrained) throws IOException, MalformedModelException {
        Model model = Model.newInstance("QNetwork");
        model.setBlock(NetworkModel.getBlock());
        if (usePretrained) {
            model.load(Paths.get(MODEL_PATH), PARAMS_PREFIX);
        }
        return model;
    }

    public static SequentialBlock getBlock() {
        // conv -> conv -> conv -> fc -> fc
        return new SequentialBlock()
                .add(Conv2d.builder()
                        .setKernelShape(new Shape(8, 8))
                        .optStride(new Shape(4, 4))
                        .optPadding(new Shape(3, 3))
                        .setFilters(4).build())
                .add(Activation::relu)

                .add(Conv2d.builder()
                        .setKernelShape(new Shape(4, 4))
                        .optStride(new Shape(2, 2))
                        .setFilters(32).build())
                .add(Activation::relu)

                .add(Conv2d.builder()
                        .setKernelShape(new Shape(3, 3))
                        .optStride(new Shape(1, 1))
                        .setFilters(64).build())
                .add(Activation::relu)

                .add(Blocks.batchFlattenBlock())
                .add(Linear
                        .builder()
                        .setUnits(512).build())
                .add(Activation::relu)

                .add(Linear
                        .builder()
                        .setUnits(Command.values().length).build());

    }

}
