package ru.ifmo.app.shared.commands;

import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.utils.Messages;

public class GroupCountingByIdCommand implements Command {

    @Override
    public void execute(CommandContext context, Object commandParameters)
        throws InvalidCommandParametersException, ExitProgramException {
        var grouped = context
            .vehicles()
            .stream()
            .map(vehicle -> {
                var withSameId = context
                    .vehicles()
                    .stream()
                    .filter(v -> v.id() == vehicle.id())
                    .count();
                return new SimpleEntry<>(vehicle.id(), withSameId);
            });

        var joined = grouped
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n"));

        if (joined.equals("")) {
            context.outputWriter().println(Messages.get("CollectionIsEmpty"));
            return;
        }
        context.outputWriter().println(joined);
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.GroupCountingById");
    }
}
