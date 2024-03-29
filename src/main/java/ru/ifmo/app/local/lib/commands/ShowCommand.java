package ru.ifmo.app.local.lib.commands;

import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.utils.Messages;

/**
 * Command used to log the currently stored collection. Uses
 * {@link ru.ifmo.app.shared.entities.Vehicle#toString Vehicle#toString} method to log each element
 * in the collection.
 */
public class ShowCommand implements DeprecatedCommand {

    @Override
    public void execute(DeprecatedCommandContext context) {
        String searchString = context.arguments().length == 0
            ? ""
            : context.arguments()[0];

        var stream = context.vehicles().stream();

        stream
            .map(Vehicle::toString)
            .filter(vehicle -> vehicle.contains(searchString))
            .forEach(vehicle -> {
                App.logger.info(vehicle.toString());
            });

        if (context.vehicles().stream().findAny().isEmpty()) {
            App.logger.info(Messages.get("CollectionIsEmpty"));
        }
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Show");
    }
}
