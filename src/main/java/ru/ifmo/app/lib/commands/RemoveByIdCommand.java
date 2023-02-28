package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.utils.Messages;

public class RemoveByIdCommand implements Command {
    @Override
    public void execute(CommandContext context) throws InvalidNumberOfArgumentsException {
        if (context.arguments().length < 1)
            throw new InvalidNumberOfArgumentsException(1, context.arguments().length);
        
        String vehicleUUID = context.arguments()[0];

        var found = context.vehicles().removeIf(v -> {
            if (v.id().toString().startsWith(vehicleUUID)) {
                App.logger.info(Messages.get("RemovingVehicleWithId", v.id()));
                return true;
            }
            return false;
        });

        if (!found) {
            App.logger.warn(Messages.get("Warn.VehicleStartingWithIdNotFound", vehicleUUID));
        }
    }

    @Override
    public String[] helpArguments() {
        return new String[] {
            Messages.get("Help.Command.Arg.Id")
        };
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.RemoveById");
    }
}
