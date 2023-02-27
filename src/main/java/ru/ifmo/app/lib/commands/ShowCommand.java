package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;

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
            App.logger.info("Collection is empty");
        }
    }

    @Override
    public String helpMessage() {
        return "Prints out every element of the collection";
    }
}
