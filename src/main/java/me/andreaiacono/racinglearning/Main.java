package me.andreaiacono.racinglearning;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.gui.MainFrame;
//import org.nd4j.jita.conf.CudaEnvironment;

public class Main {

    public static void main(String[] args) throws Exception {
//        CudaEnvironment.getInstance().getConfiguration().allowMultiGPU(true);
        GameParameters params = new GameParameters(args);
        new MainFrame(params);
    }
}
