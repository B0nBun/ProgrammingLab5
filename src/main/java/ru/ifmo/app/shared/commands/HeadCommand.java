package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.utils.Messages;

public class HeadCommand implements Command {

    @Override
    public void execute(
        CommandContext context,
        Object commandParameters,
        Serializable additionalObject
    ) throws InvalidCommandParametersException, ExitProgramException {
        Vehicle head = context.vehicles().stream().sorted().findFirst().orElse(null);
        if (head == null) {
            context.outputWriter().println(Messages.get("NoFirstElement"));
            return;
        }
        context.outputWriter().println(head.toString());
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Head");
    }
}
