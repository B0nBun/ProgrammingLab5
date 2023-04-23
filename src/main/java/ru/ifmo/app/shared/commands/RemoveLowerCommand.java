package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import java.util.Scanner;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.shared.utils.Messages;

public class RemoveLowerCommand implements Command {

    @Override
    public Serializable additionalObjectFromScanner(Scanner scanner, boolean logScanned) {
        return VehicleCreationSchema.createFromScanner(scanner, logScanned);
    }

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        var creationSchema = (VehicleCreationSchema) additionalObject;
        var notAddedVehicle = creationSchema.generate(
            context.vehicles().peekNextId(),
            context.vehicles().peekNextCreationDate()
        );
        context
            .vehicles()
            .removeIf(vehicle -> {
                if (vehicle.compareTo(notAddedVehicle) < 0) {
                    context
                        .outputWriter()
                        .println(Messages.get("RemovingVehicleWithId", vehicle.id()));
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
