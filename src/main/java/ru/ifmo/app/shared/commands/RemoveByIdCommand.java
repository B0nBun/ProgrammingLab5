package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.utils.Messages;

public class RemoveByIdCommand implements Command {

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
    public RemoveByIdCommand.Parameters parametersObjectFromStrings(String[] strings)
        throws InvalidCommandParametersException {
        if (strings.length < 1) throw new InvalidCommandParametersException(
            "Expected: \n" +
            CommandParameters.description(RemoveByIdCommand.Parameters.class)
        );

        String vehicleUUID = strings[0];

        return new RemoveByIdCommand.Parameters(vehicleUUID);
    }

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        try {
            var parameters = (Parameters) commandParameters;
            var idString = parameters.idString;

            var found = context
                .vehicles()
                .removeIf(v -> {
                    if (v.id().toString().startsWith(idString)) {
                        context
                            .outputWriter()
                            .println(Messages.get("RemovingVehicleWithId", v.id()));
                        return true;
                    }
                    return false;
                });

            if (!found) {
                context
                    .outputWriter()
                    .println(
                        Messages.get("Warn.VehicleStartingWithIdNotFound", idString)
                    );
            }
        } catch (ClassCastException err) {
            throw new InvalidCommandParametersException(
                CommandParameters.description(RemoveByIdCommand.Parameters.class)
            );
        }
    }

    @Override
    public String helpArguments() {
        return CommandParameters.description(RemoveByIdCommand.Parameters.class);
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.RemoveById");
    }
}
