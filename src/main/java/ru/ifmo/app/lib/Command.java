package ru.ifmo.app.lib;

import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.utils.Messages;

public interface Command {
    public void execute(CommandContext arguments) throws InvalidArgumentException, InvalidNumberOfArgumentsException, ExitProgramException;

    default public String[] helpArguments() {
        return new String[0];
    }
    
    default public String helpMessage() {
        return Messages.get("Help.Command.NoHelpDescription");
    }
}
