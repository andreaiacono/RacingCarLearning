package me.andreaiacono.racinglearning.misc;

        import me.andreaiacono.racinglearning.core.Command;

public class CommandsTranslator {

    public static int getIntegerFromCommand(Command command) {
        return command.ordinal();
    }

    public static Command getCommandFromInteger(Integer value) {
        return Command.values()[value];
    }
}
