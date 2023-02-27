package ru.ifmo.app.lib.commands;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.utils.Messages;

public class HelpCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        Stream<String> commandStrings = context.commandRegistery().getAllCommands().stream()
            .map(entry -> {
                var command = entry.getValue();
                var helpMessage = command.helpMessage();
                var commandAliases = "[" + String.join(", ", entry.getKey()) + "]";
                var argumentsString = String.join(" ", command.helpArguments());
                return "- " + commandAliases + " " + argumentsString + "\n" + helpMessage;
            });
        
        App.logger.info("List of commands:");
        App.logger.info(commandStrings.collect(Collectors.joining("\n")));
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Help");
    }
}
