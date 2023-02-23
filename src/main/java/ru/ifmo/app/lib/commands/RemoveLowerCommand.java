package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.exceptions.RuntimeIOException;

public class RemoveLowerCommand implements Command {
    @Override
    public void execute(CommandContext context) throws IOException {
        var creationSchema = VehicleCreationSchema.createFromScanner(context.scanner(), context.writer());
        var notAddedVehicle = creationSchema.generate(context.vehicles().peekNextId(), context.vehicles().peekNextCreationDate());
        try {
            context.vehicles().removeIf(vehicle -> {
                if (vehicle.compareTo(notAddedVehicle) < 0) {
                    try {
                        Utils.print(context.writer(), "Removing vehicle with id = " + vehicle.id() + "\n");
                    } catch (IOException err) {
                        throw new RuntimeIOException(err);
                    }
                    return true;
                }
                return false;
            });
        } catch (RuntimeIOException err) {
            throw err.iocause;
        }
    }

    @Override
    public String helpMessage() {
        return "Remove from the collection all of the elements, which are lower than a specified one";
    }
}
