package me.andreaiacono.racinglearning;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.gui.MainFrame;
import me.andreaiacono.racinglearning.misc.Constants;
import org.nd4j.jita.conf.CudaEnvironment;
//import org.nd4j.jita.conf.CudaEnvironment;

public class Main {

    public static void main(String[] args) throws Exception {
        // PLEASE NOTE: For CUDA FP16 precision support is available
//        DataTypeUtil.setDTypeForContext(DataBuffer.Type.HALF);

// temp workaround for backend initialization
//        Nd4j.create(1);
//
        CudaEnvironment.getInstance().getConfiguration()
                // key option enabled
                .allowMultiGPU(true)

                // we're allowing larger memory caches
                .setMaximumDeviceCache(4L * 1024L * 1024L * 1024L)

                // cross-device access is used for faster model averaging over pcie
                .allowCrossDeviceAccess(true);

        GameParameters params = new GameParameters(args);
        Constants.SCREEN_SIZE = params.getInt(GameParameters.SCREEN_SIZE_PARAM, Constants.DEFAULT_SCREEN_SIZE);
        new MainFrame(params);
    }
}
