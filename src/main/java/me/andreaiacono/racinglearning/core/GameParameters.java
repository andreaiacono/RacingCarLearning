package me.andreaiacono.racinglearning.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class GameParameters {

    private final CommandLine cmd;
    public final static String TYPE_PARAM = "t";
    public final static String DRAW_INFO_PARAM = "i";
    public final static String USE_BW_PARAM = "b";
    public static final String MODEL_NAME_PARAM = "m";
    public static final String SIZE_PARAM = "s";

    public enum Type {
        HUMAN, MACHINE_LEARN, MACHINE_RACE;
    }

    public GameParameters(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(TYPE_PARAM, true, "Racing type: [HUMAN, LEARN, RACE]");
        options.addOption(DRAW_INFO_PARAM, false, "Draws info on the screen");
        options.addOption(USE_BW_PARAM, false, "Draws the screen in Black&White");
        options.addOption(MODEL_NAME_PARAM, true, "The filename of the model to load/save");
        options.addOption(SIZE_PARAM, true, "The size in pixel of the screen");

        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse( options, args);

        if (cmd.getOptionValue(GameParameters.TYPE_PARAM) == null) {
            System.out.println("No type argument specified. Argument -t can be [HUMAN, MACHINE_LEARN, MACHINE_RACE]");
            System.exit(-1);
        }
    }

    public boolean isProvided(String paramName) {
        return cmd.hasOption(paramName);
    }

    public String getValue(String paramName) {
        return cmd.getOptionValue(paramName);
    }

    public boolean getBool(String paramName) {
        return cmd.hasOption(paramName);
    }

}
