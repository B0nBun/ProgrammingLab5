package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lib.Command;
import static lib.Utils.print;
import lib.Vehicles;
import lib.exceptions.CommandNotFoundException;
import lib.exceptions.InvalidArgumentException;

public class HelpCommand implements Command {
    @Override
    public void execute(
        String[] _arguments,
        Vehicles _vehicles,
        Scanner _scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws CommandNotFoundException, InvalidArgumentException, IOException {
        Stream<String> commandStrings = commandsMap.entrySet().stream()
            .map(entry -> "- " + entry.getKey() + "\n" + entry.getValue().helpMessage());
        
        print(writer, (
            "List of commands:\n" +
            commandStrings.collect(Collectors.joining("\n")) +
            "\n"
        ));
    }

    @Override
    public String helpMessage() {
        return "Prints out a reference for all of the available commands";
    }
}
