package ru.ifmo.app.local.lib.commands;

import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.shared.utils.Messages;

/**
 * Command used to log out the count of the elements in the collection grouped by the id. If the
 * collection is empty, then the appropriate message is logged.
 */
public class GroupCountingByIdCommand implements DeprecatedCommand {

    @Override
    public void execute(DeprecatedCommandContext context) {
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
