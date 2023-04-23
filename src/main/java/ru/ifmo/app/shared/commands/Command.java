package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import java.util.Scanner;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.SerializableDummy;
import ru.ifmo.app.shared.utils.Messages;

public interface Command {
    public default Serializable additionalObjectFromScanner(
        Scanner scanner,
        boolean logScanned
    ) {
        return SerializableDummy.singletone;
    }

    public default CommandParameters parametersObjectFromStrings(String[] strings)
        throws InvalidCommandParametersException {
        return CommandParameters.dummy;
    }

    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException;

    /**
     * A method which is called in the {@link ru.ifmo.app.local.lib.commands.HelpCommand HelpCommand}
     * to show what kind of arguments are needed. By default returns an empty array.
     *
     * @return an array of arguments, with strings as the arguments' names/types (e.g. {@code [ "id"
     *     ]})
     */
    public default String helpArguments() {
        return "";
    }

    /**
     * A method which is called in the {@link ru.ifmo.app.local.lib.commands.HelpCommand HelpCommand}
     * to describe the use of the command. By default returns a "no description" message
     *
     * @return A description of the command
     */
    public default String helpMessage() {
        return Messages.get("Help.Command.NoHelpDescription");
    }
}
