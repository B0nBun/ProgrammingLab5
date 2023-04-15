package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import java.util.Scanner;
import ru.ifmo.app.local.lib.exceptions.ValidationException;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.utils.Messages;

public class AddIfMaxCommand implements Command {

    @Override
    public Serializable additionalObjectFromScanner(Scanner scanner) {
        VehicleCreationSchema creationSchema = VehicleCreationSchema.createFromScanner(
            scanner,
            false
        );
        return creationSchema;
    }

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        try {
            var vehicleSchema = (VehicleCreationSchema) additionalObject;
            vehicleSchema.validate();
            var maxVehicle = context
                .vehicles()
                .stream()
                .max(Vehicle::compareTo)
                .orElse(null);
            if (
                maxVehicle == null ||
                vehicleSchema
                    .generate(
                        context.vehicles().peekNextId(),
                        context.vehicles().peekNextCreationDate()
                    )
                    .compareTo(maxVehicle) >
                0
            ) {
                Vehicle added = context.vehicles().add(vehicleSchema);
                context
                    .outputWriter()
                    .println("The vehicle with id='" + added.id() + "' was added");
            } else {
                context
                    .outputWriter()
                    .println(
                        "The vehicle couldn't be added, since it is not a max element"
                    );
            }
        } catch (ValidationException err) {
            context
                .outputWriter()
                .println("Validation error on server side: " + err.getMessage());
        }
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.AddIfMax");
    }
}
