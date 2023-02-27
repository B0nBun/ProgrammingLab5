package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;

public class InfoCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        App.logger.info("Creation Date: {}", context.vehicles().creationDate());
        App.logger.info("Collection Type: {}", context.vehicles().collectionType());
        App.logger.info("Collection Size: {}", context.vehicles().stream().count());
    }

    @Override
    public String helpMessage() {
        return "Print out the info about current collection";
    }
}
