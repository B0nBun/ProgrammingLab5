package ru.ifmo.app.local.lib.commands;

import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.local.lib.exceptions.ExitProgramException;
import ru.ifmo.app.shared.utils.Messages;

/** Command used to exit the program. All it does is throw the {@link ExitProgramException} */
public class ExitCommand implements DeprecatedCommand {
  @Override
  public void execute(DeprecatedCommandContext context) throws ExitProgramException {
    throw new ExitProgramException();
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Exit");
  }
}
