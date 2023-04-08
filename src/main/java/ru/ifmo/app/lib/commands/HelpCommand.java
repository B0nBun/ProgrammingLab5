package ru.ifmo.app.lib.commands;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.ifmo.app.App;
import ru.ifmo.app.lib.DeprecatedCommand;
import ru.ifmo.app.lib.DeprecatedCommandContext;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to log out the helping message. Help message contains all of the commands in the
 * passed {@link ru.ifmo.app.lib.utils.DeprecatedCommandRegistery} with arguments and help
 * descriptions.
 */
public class HelpCommand implements DeprecatedCommand {
  @Override
  public void execute(DeprecatedCommandContext context) {
    Stream<String> commandStrings =
        context.commandRegistery().getAllCommands().stream().map(entry -> {
          var command = entry.getValue();
          var helpMessage = command.helpMessage();
          var commandAliases = "[" + String.join(", ", entry.getKey()) + "]";
          var argumentsString = String.join(" ", command.helpArguments());
          return "- " + commandAliases + " " + argumentsString + "\n" + helpMessage;
        });

    App.logger.info(Messages.get("Help.CommandListTitle"));
    App.logger.info(commandStrings.collect(Collectors.joining("\n")));
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Help");
  }
}
