package lib;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.exceptions.CommandNotFoundException;
import lib.exceptions.InvalidArgumentException;

public interface Command {
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws CommandNotFoundException, InvalidArgumentException, IOException;

    default public String helpMessage() {
        return "No 'help' description";
    }
}
