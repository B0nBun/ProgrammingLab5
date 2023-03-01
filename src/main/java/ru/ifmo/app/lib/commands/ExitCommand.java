package ru.ifmo.app.lib.commands;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.utils.Messages;

/** Command used to exit the program. All it does is throw the {@link ExitProgramException} */
public class ExitCommand implements Command {
  @Override
  public void execute(CommandContext context) throws ExitProgramException {
    throw new ExitProgramException();
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Exit");
  }
}
