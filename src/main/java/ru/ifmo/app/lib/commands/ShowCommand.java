package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to log the currently stored collection.
 * Uses {@link ru.ifmo.app.lib.entities.Vehicle#toString Vehicle#toString} method to log each element in the collection.
 */
public class ShowCommand implements Command {

    private class BooleanBox {
        public boolean value;
        public BooleanBox(boolean value) {
            this.value = value;
        }
    }
    
    @Override
    public void execute(CommandContext context) {
        var stream = context.vehicles().stream();

        var isEmpty = new BooleanBox(true);            
        stream.forEach(vehicle -> {
            isEmpty.value = false;
            App.logger.info(vehicle.toString());
        });

        if (isEmpty.value) {
            App.logger.info(Messages.get("CollectionIsEmpty"));
        }
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Show");
    }
}
