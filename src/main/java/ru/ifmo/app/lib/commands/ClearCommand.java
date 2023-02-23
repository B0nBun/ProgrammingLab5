package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;

public class ClearCommand implements Command {
    @Override
    public void execute(CommandContext context) throws IOException {
        context.vehicles().clear();        
        Utils.print(context.writer(), "Collection cleared!\n");
    }

    @Override
    public String helpMessage() {
        return "Clears the collection";
    }
}
