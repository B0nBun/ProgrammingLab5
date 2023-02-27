package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.utils.Messages;

public class RemoveLowerCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        var creationSchema = VehicleCreationSchema.createFromScanner(context.scanner(), context.writer());
        var notAddedVehicle = creationSchema.generate(context.vehicles().peekNextId(), context.vehicles().peekNextCreationDate());
        context.vehicles().removeIf(vehicle -> {
            if (vehicle.compareTo(notAddedVehicle) < 0) {
                App.logger.info("Removing vehicle with id='{}'", vehicle.id());
                return true;
            }
            return false;
        });
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.RemoveLowerCommand");
    }
}
