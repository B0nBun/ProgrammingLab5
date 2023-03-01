package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to log the info about the collection.
 * Logs collection's creation date, type and size
 */
public class InfoCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        App.logger.info(Messages.get("InfoCommand.CreationDate"), context.vehicles().creationDate());
        App.logger.info(Messages.get("InfoCommand.CollectionType"), context.vehicles().collectionType());
        App.logger.info(Messages.get("InfoCommand.CollectionSize"), context.vehicles().stream().count());
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Info");
    }
}
