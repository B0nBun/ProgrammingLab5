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

public class AddCommand implements Command {

    @Override
    public Serializable additionalObjectFromScanner(Scanner scanner, boolean logScanned) {
        VehicleCreationSchema creationSchema = VehicleCreationSchema.createFromScanner(
            scanner,
            logScanned
        );
        return creationSchema;
    }

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        var vehicleSchema = (VehicleCreationSchema) additionalObject;
        try {
            vehicleSchema.validate();
            Vehicle added = context.vehicles().add(vehicleSchema);
            context
                .outputWriter()
                .println("The vehicle with id='" + added.id() + "' was added");
        } catch (ValidationException err) {
            context
                .outputWriter()
                .println("Validation error on server side: " + err.getMessage());
        }
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Add");
    }
}
