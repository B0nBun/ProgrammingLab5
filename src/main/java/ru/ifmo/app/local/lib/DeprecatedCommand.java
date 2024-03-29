package ru.ifmo.app.local.lib;

import ru.ifmo.app.local.lib.exceptions.ExitProgramException;
import ru.ifmo.app.local.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.local.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.local.lib.exceptions.MaximumScriptExecutionDepthException;
import ru.ifmo.app.shared.utils.Messages;

/** Interface which signifies that the given class can be used to exexcute some kind of command. */
public interface DeprecatedCommand {
    /**
     * @param context A {@link DeprecatedCommandContext} object, which contains all the data needed by
     *        the commands
     * @throws InvalidArgumentException Thrown if the command argument couldn't be parsed or has the
     *         wrong "type" (e.g. "count_greater_than_fuel_type asdasdasdasd")
     * @throws InvalidNumberOfArgumentsException Thrown if some of the neccesary arguments were
     *         missing
     * @throws ExitProgramException Thrown if the command signifies the exit from the program (e.g.
     *         {@link ru.ifmo.app.local.lib.commands.ExitCommand ExitCommand})
     * @throws MaximumScriptExecutionDepthException Thrown if the scirpt execution depth exceeds
     *         maximum allowed value in {@link ru.ifmo.app.local.lib.commands.ExecuteScriptCommand}
     */
    public void execute(DeprecatedCommandContext context)
        throws InvalidArgumentException, InvalidNumberOfArgumentsException, ExitProgramException, MaximumScriptExecutionDepthException;

    /**
     * A method which is called in the {@link ru.ifmo.app.local.lib.commands.HelpCommand HelpCommand}
     * to show what kind of arguments are needed. By default returns an empty array.
     *
     * @return an array of arguments, with strings as the arguments' names/types (e.g. {@code [ "id"
     *     ]})
     */
    public default String[] helpArguments() {
        return new String[0];
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
