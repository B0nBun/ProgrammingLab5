package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.ifmo.app.lib.Command;
import static ru.ifmo.app.lib.Utils.print;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;

public class HelpCommand implements Command {
    @Override
    public void execute(
        String[] _arguments,
        Vehicles _vehicles,
        Scanner _scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws IOException {
        Stream<String> commandStrings = commandsRegistery.getAllCommands().stream()
            .map(entry -> {
                var command = entry.getValue();
                var helpMessage = command.helpMessage();
                var commandAliases = "[" + String.join(", ", entry.getKey()) + "]";
                var argumentsString = String.join(" ", command.helpArguments());
                return "- " + commandAliases + " " + argumentsString + "\n" + helpMessage;
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
