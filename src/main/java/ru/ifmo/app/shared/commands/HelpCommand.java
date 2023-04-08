package ru.ifmo.app.shared.commands;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.ifmo.app.lib.utils.Messages;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.Command;

public class HelpCommand implements Command {
  @Override
  public void execute(CommandContext context, Object _commandParameters)
      throws InvalidCommandParametersException, ExitProgramException {
    Stream<String> commandStrings =
        context.commandRegistery().getAllCommands().stream().map(entry -> {
          var command = entry.getValue();
          var helpMessage = command.helpMessage();
          var commandAliases = "[" + String.join(", ", entry.getKey()) + "]";
          var argumentsString = String.join(" ", command.helpArguments());
          return "- " + commandAliases + " " + argumentsString + "\n" + helpMessage;
        });

    context.outputWriter().println(Messages.get("Help.CommandListTitle"));
    context.outputWriter().println(commandStrings.collect(Collectors.joining("\n")));
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Help");
  }
}
