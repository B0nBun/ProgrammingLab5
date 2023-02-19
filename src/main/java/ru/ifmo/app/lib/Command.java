package ru.ifmo.app.lib;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.Utils.CommandRegistery;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

public interface Command {
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandRegistery
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException, ExitProgramException;

    default public String[] helpArguments() {
        return new String[0];
    }
    
    default public String helpMessage() {
        return "No 'help' description";
    }
}
