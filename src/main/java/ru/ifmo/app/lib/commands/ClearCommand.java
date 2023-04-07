package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.DeprecatedCommand;
import ru.ifmo.app.lib.DeprecatedCommandContext;
import ru.ifmo.app.lib.utils.Messages;

/** Command used to clear out the collection. */
public class ClearCommand implements DeprecatedCommand {
  @Override
  public void execute(DeprecatedCommandContext context) {
    context.vehicles().clear();
    App.logger.info(Messages.get("CollectionCleared"));
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Clear");
  }
}
