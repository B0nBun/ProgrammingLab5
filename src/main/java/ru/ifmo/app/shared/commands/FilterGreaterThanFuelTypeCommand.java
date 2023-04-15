package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import java.util.stream.Collectors;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.entities.FuelType;
import ru.ifmo.app.shared.utils.Messages;

public class FilterGreaterThanFuelTypeCommand implements Command {

    private static class Parameters extends CommandParameters {

        @CommandParameter(name = "fuel-type")
        public FuelType fuelType;

        public Parameters(FuelType fuelType) {
            this.fuelType = fuelType;
        }
    }

    @Override
    public CommandParameters parametersObjectFromStrings(String[] strings)
        throws InvalidCommandParametersException {
        if (strings.length < 1) throw new InvalidCommandParametersException(
            "Expected: " + CommandParameters.description(Parameters.class)
        );
        try {
            FuelType chosenType = FuelType.parse(strings[0]);
            return new Parameters(chosenType);
        } catch (ParsingException err) {
            throw new InvalidCommandParametersException(
                Messages.get("Vehicle.VehicleType") + ": " + err.getMessage()
            );
        }
    }

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        var params = (Parameters) commandParameters;
        FuelType chosen = params.fuelType;
        var result = context
            .vehicles()
            .stream()
            .filter(v -> v.fuelType().compareTo(chosen) > 0)
            .map(v -> v.toString())
            .collect(Collectors.joining("\n"))
            .trim();

        if (result.equals("")) {
            context.outputWriter().println(Messages.get("NoElementsWithGreaterFuelType"));
            return;
        }

        context.outputWriter().println(result);
    }

    @Override
    public String helpArguments() {
        return CommandParameters.description(Parameters.class);
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.FilterGreaterThanFuelType");
    }
}
