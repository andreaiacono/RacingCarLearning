package me.andreaiacono.racinglearning;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.gui.MainFrame;

public class Main {

    public static void main(String[] args) throws Exception {
        GameParameters params = new GameParameters(args);
        new MainFrame(params);
    }
}
