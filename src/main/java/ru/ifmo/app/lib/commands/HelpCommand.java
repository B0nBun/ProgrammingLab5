package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.ifmo.app.lib.Command;
import static ru.ifmo.app.lib.Utils.print;
import ru.ifmo.app.lib.Vehicles;

public class HelpCommand implements Command {
    @Override
    public void execute(
        String[] _arguments,
        Vehicles _vehicles,
        Scanner _scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        Stream<String> commandStrings = commandsMap.entrySet().stream()
            .map(entry -> {
                var command = entry.getValue();
                var argumentsString = String.join(" ", command.helpArguments());
                return "- " + entry.getKey() + " " + argumentsString + "\n" + command.helpMessage();
            });
        
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
