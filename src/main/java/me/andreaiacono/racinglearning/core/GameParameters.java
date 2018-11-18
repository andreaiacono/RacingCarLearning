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
    public static final String TRACK_DURATION = "d";
    public static final String SCALE_PARAM = "c";
    public static final String EASY_PARAM = "e";

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
        options.addOption(SCALE_PARAM, true, "The scale of the shown track");
        options.addOption(EASY_PARAM, true, "The percentage of an easy track");
        options.addOption(TRACK_DURATION, true, "The number of epochs after which the track is changed");

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

    public int getValueWithDefault(String paramName, int defaultValue) {
        return isProvided(paramName) ? Integer.valueOf(cmd.getOptionValue(paramName)) : defaultValue;
    }

    public double getDouble(String paramName) {
        return Double.parseDouble(cmd.getOptionValue(paramName));
    }

    public boolean getBool(String paramName) {
        return cmd.hasOption(paramName);
    }

}
