package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.utils.Messages;

public class ShowCommand implements Command {

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        var stream = context.vehicles().stream();
        stream
            .map(Vehicle::toString)
            .forEach(vehicle -> {
                context.outputWriter().println(vehicle);
            });
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Show");
    }
}
