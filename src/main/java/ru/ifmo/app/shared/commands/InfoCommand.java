package ru.ifmo.app.shared.commands;

import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.utils.Messages;

public class InfoCommand implements Command {

    @Override
    public void execute(CommandContext context, Object commandParameters)
        throws InvalidCommandParametersException, ExitProgramException {
        context
            .outputWriter()
            .println(
                Messages.get(
                    "InfoCommand.CreationDate",
                    context.vehicles().creationDate()
                )
            );
        context
            .outputWriter()
            .println(
                Messages.get(
                    "InfoCommand.CollectionType",
                    context.vehicles().collectionType()
                )
            );
        context
            .outputWriter()
            .println(
                Messages.get(
                    "InfoCommand.CollectionSize",
                    context.vehicles().stream().count()
                )
            );
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Info");
    }
}
