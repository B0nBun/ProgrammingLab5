package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.entities.Vehicle;

public class HeadCommand implements Command {
    @Override
    public void execute(CommandContext context) throws IOException {
        Vehicle head = context.vehicles().stream().sorted().findFirst().orElse(null);
        if (head == null) {
            Utils.print(context.writer(), "The collection is empty, so there is no first element\n");
            return;
        }
        Utils.print(context.writer(), head.toString() + "\n");
    }

    @Override
    public String helpMessage() {
        return "Prints out the first element in the collection";
    }
}
