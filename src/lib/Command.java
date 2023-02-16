package lib;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.exceptions.InvalidArgumentException;
import lib.exceptions.InvalidNumberOfArgumentsException;

public interface Command {
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException;

    default public String helpMessage() {
        return "No 'help' description";
    }
}
