package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;

public class ClearCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        context.vehicles().clear();        
        App.logger.info("Collection cleared!");
    }

    @Override
    public String helpMessage() {
        return "Clears the collection";
    }
}
