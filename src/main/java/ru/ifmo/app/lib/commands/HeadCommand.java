package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to log out the frist element in the collection. If the collection is empty, then the
 * appropriate message is logged.
 */
public class HeadCommand implements Command {
  @Override
  public void execute(CommandContext context) {
    Vehicle head = context.vehicles().stream().sorted().findFirst().orElse(null);
    if (head == null) {
      App.logger.info(Messages.get("NoFirstElement"));
      return;
    }
    App.logger.info(head.toString());
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Head");
  }
}
