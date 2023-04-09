package ru.ifmo.app.local.lib.commands;

import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.shared.utils.Messages;

/**
 * Command used to log the info about the collection. Logs collection's creation date, type and size
 */
public class InfoCommand implements DeprecatedCommand {
  @Override
  public void execute(DeprecatedCommandContext context) {
    App.logger.info(Messages.get("InfoCommand.CreationDate", context.vehicles().creationDate()));
    App.logger
        .info(Messages.get("InfoCommand.CollectionType", context.vehicles().collectionType()));
    App.logger
        .info(Messages.get("InfoCommand.CollectionSize", context.vehicles().stream().count()));
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Info");
  }
}
