package ru.ifmo.app.lib.commands;


import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.exceptions.ExitProgramException;

public class ExitCommand implements Command {
    @Override
    public void execute(CommandContext context) throws ExitProgramException {
        throw new ExitProgramException();
    }

    @Override
    public String helpMessage() {
        return "Exits the program";
    }
}
