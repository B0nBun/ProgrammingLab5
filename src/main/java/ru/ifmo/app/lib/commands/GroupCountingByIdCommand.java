package ru.ifmo.app.lib.commands;

import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.utils.Messages;

public class GroupCountingByIdCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        var grouped = context.vehicles()
            .stream()
            .map(vehicle -> {
                var withSameId = context.vehicles()
                    .stream()
                    .filter(v -> v.id() == vehicle.id())
                    .count();
                return new SimpleEntry<>(vehicle.id(), withSameId);
            });

        var joined = grouped
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n"));
        
        if (joined.equals("")) {
            App.logger.info(Messages.get("CollectionIsEmpty"));
            return;
        }
        App.logger.info(joined);
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.GroupCountingById");
    }
}
