package me.andreaiacono.racinglearning.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class GameParameters {

    private final CommandLine cmd;
    public final static String TYPE_PARAM = "t";
    public final static String DRAW_INFO_PARAM = "i";
    public final static String DRAW_CHECKPOINTS = "k";
    public final static String BATCH_SIZE_PARAM = "b";
    public static final String MODEL_NAME_PARAM = "m";
    public static final String SCREEN_SIZE_PARAM = "s";
    public static final String TRACK_DURATION = "d";
    public static final String MAGNIFY_PARAM = "g";
    public static final String SHOW_GRAPH_PARAM = "h";
    public static final String SHOW_TRACK = "o";
    public static final String EASY_PARAM = "e";
    public static final String SIMPLE_CAR_PARAM = "p";
    public static final String TILES_NUMBER_PARAM = "n";


    public enum Type {
        HUMAN, TRAIN, RACE;
    }

    public GameParameters(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(TYPE_PARAM, true, "Racing type: [HUMAN, TRAIN, RACE]");
        options.addOption(DRAW_INFO_PARAM, false, "Draws info on the screen");
        options.addOption(DRAW_CHECKPOINTS, false, "Draws the checkpoints in the track");
        options.addOption(SHOW_GRAPH_PARAM, false, "Shows the graph window");
        options.addOption(SHOW_TRACK, false, "Shows the track window");
        options.addOption(BATCH_SIZE_PARAM, true, "The size of the mini batch. Defaults to 32.");
        options.addOption(MODEL_NAME_PARAM, true, "The filename of the model to load/save");
        options.addOption(SCREEN_SIZE_PARAM, true, "The size in pixel of the screen");
        options.addOption(MAGNIFY_PARAM, true, "The magnifier for the screen window");
        options.addOption(TILES_NUMBER_PARAM, true, "The number of tiles for generating the random track");
        options.addOption(SIMPLE_CAR_PARAM, false, "Draws a simple car (a square) instead of a more complex");
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

    public int getInt(String paramName, int defaultValue) {
        return isProvided(paramName) ? Integer.parseInt(cmd.getOptionValue(paramName)) : defaultValue;
    }

    public float getFloat(String paramName, float defaultValue) {
        return isProvided(paramName) ? Float.parseFloat(cmd.getOptionValue(paramName)) : defaultValue;
    }
    public boolean getBool(String paramName) {
        return cmd.hasOption(paramName);
    }

}
