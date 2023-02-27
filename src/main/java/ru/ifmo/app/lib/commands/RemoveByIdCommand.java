package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

public class RemoveByIdCommand implements Command {
    @Override
    public void execute(CommandContext context) throws InvalidNumberOfArgumentsException {
        if (context.arguments().length < 1)
            throw new InvalidNumberOfArgumentsException(1, context.arguments().length);
        
        String vehicleUUID = context.arguments()[0];

        var found = context.vehicles().removeIf(v -> {
            if (v.id().toString().startsWith(vehicleUUID)) {
                App.logger.info("Removing vehicle with id='{}''...", v.id());
                return true;
            }
            return false;
        });

        if (!found) {
            App.logger.warn("Vehicle with id starting with '{}' not found", vehicleUUID);
        }
    }

    @Override
    public String[] helpArguments() {
        return new String[] {"id"};
    }

    @Override
    public String helpMessage() {
        return "Remove element with specified 'id'";
    }
}
