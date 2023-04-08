package ru.ifmo.app.shared;

import ru.ifmo.app.lib.utils.Messages;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;

public interface Command {
  default public Object parametersObjectFromStrings(String[] strings) {
    return new Object();
  };

  public void execute(CommandContext context, Object commandParameters)
      throws InvalidCommandParametersException, ExitProgramException;

  /**
   * A method which is called in the {@link ru.ifmo.app.lib.commands.HelpCommand HelpCommand} to
   * show what kind of arguments are needed. By default returns an empty array.
   *
   * @return an array of arguments, with strings as the arguments' names/types (e.g. {@code [ "id"
   *     ]})
   */
  public default String[] helpArguments() {
    return new String[0];
  }

  /**
   * A method which is called in the {@link ru.ifmo.app.lib.commands.HelpCommand HelpCommand} to
   * describe the use of the command. By default returns a "no description" message
   *
   * @return A description of the command
   */
  public default String helpMessage() {
    return Messages.get("Help.Command.NoHelpDescription");
  }
}
