package ru.ifmo.app.lib;

import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Interface which signifies that the given class can be used to exexcute some kind of command.
 */
public interface Command {
    /** 
     * @param context A {@link CommandContext} object, which contains all the data needed by the commands
     * 
     * @throws InvalidArgumentException Thrown if the command argument couldn't be parsed or has the wrong "type" (e.g. "count_greater_than_fuel_type asdasdasdasd")
     * @throws InvalidNumberOfArgumentsException Thrown if some of the neccesary arguments were missing
     * @throws ExitProgramException Thrown if the command signifies the exit from the program (e.g. {@link ru.ifmo.app.lib.commands.ExitCommand ExitCommand})
     */
    public void execute(CommandContext context) throws InvalidArgumentException, InvalidNumberOfArgumentsException, ExitProgramException;

    /**
     * A method which is called in the {@link ru.ifmo.app.lib.commands.HelpCommand HelpCommand}
     * to show what kind of arguments are needed. By default returns an empty array.
     * 
     * @return an array of arguments, with strings as the arguments' names/types (e.g. {@code [ "id" ]})
     */
    default public String[] helpArguments() {
        return new String[0];
    }
    
    /**
     * A method which is called in the {@link ru.ifmo.app.lib.commands.HelpCommand HelpCommand}
     * to describe the use of the command. By default returns a "no description" message
     * 
     * @return A description of the command
     */
    default public String helpMessage() {
        return Messages.get("Help.Command.NoHelpDescription");
    }
}
