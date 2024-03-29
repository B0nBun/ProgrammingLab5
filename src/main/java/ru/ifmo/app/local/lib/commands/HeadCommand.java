package ru.ifmo.app.local.lib.commands;

import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.utils.Messages;

/**
 * Command used to log out the frist element in the collection. If the collection is empty, then the
 * appropriate message is logged.
 */
public class HeadCommand implements DeprecatedCommand {

    @Override
    public void execute(DeprecatedCommandContext context) {
        Vehicle head = context.vehicles().stream().sorted().findFirst().orElse(null);
        if (head == null) {
            App.logger.info(Messages.get("NoFirstElement"));
            return;
        }
        App.logger.info(head.toString());
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Head");
    }
}
