package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import java.util.Scanner;
import java.util.UUID;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.utils.Messages;

public class UpdateCommand implements Command {

    @Override
    public Serializable additionalObjectFromScanner(Scanner scanner) {
        return VehicleCreationSchema.createFromScanner(scanner, false);
    }

    public static class Parameters extends CommandParameters {

        @CommandParameter(
            name = "id",
            desc = "The start of the id of the vehicle that is to be removed"
        )
        String idString;

        public Parameters(String idString) {
            this.idString = idString;
        }
    }

    @Override
    public Parameters parametersObjectFromStrings(String[] strings)
        throws InvalidCommandParametersException {
        if (strings.length < 1) throw new InvalidCommandParametersException(
            "Expected: \n" +
            CommandParameters.description(RemoveByIdCommand.Parameters.class)
        );

        String vehicleUUID = strings[0];

        return new Parameters(vehicleUUID);
    }

    private static boolean uuidStartsWith(UUID uuid, String string) {
        return uuid.toString().startsWith(string);
    }

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        var params = (Parameters) commandParameters;
        var idString = params.idString;
        var creationSchema = (VehicleCreationSchema) additionalObject;

        Vehicle found = context
            .vehicles()
            .stream()
            .filter(v -> uuidStartsWith(v.id(), idString))
            .findFirst()
            .orElseGet(() -> null);

        if (found == null) {
            context
                .outputWriter()
                .println(Messages.get("Warn.VehicleStartingWithIdNotFound", idString));
            return;
        }
        context.outputWriter().println(Messages.get("UpdatingVehicleWithId", found.id()));

        context
            .vehicles()
            .mutate(vehicle -> {
                if (uuidStartsWith(vehicle.id(), idString)) {
                    return creationSchema.generate(vehicle.id(), vehicle.creationDate());
                }
                return vehicle;
            });
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.UpdateCommand");
    }
}
