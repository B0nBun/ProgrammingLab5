package ru.ifmo.app.local.lib.commands;

import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.shared.utils.Messages;

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
