package ru.ifmo.app.lib;

import java.io.IOException;

import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

public interface Command {
    public void execute(CommandContext arguments) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException, ExitProgramException;

    default public String[] helpArguments() {
        return new String[0];
    }
    
    default public String helpMessage() {
        return "No 'help' description";
    }
}
