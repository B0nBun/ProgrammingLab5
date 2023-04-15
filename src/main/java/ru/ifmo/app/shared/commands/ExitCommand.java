package ru.ifmo.app.shared.commands;

import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.utils.Messages;

public class ExitCommand implements Command {

    @Override
    public void execute(CommandContext context, Object commandParameters)
        throws InvalidCommandParametersException, ExitProgramException {
        throw new ExitProgramException();
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Exit");
    }
}
