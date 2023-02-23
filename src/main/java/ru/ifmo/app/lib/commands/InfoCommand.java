package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;

import static ru.ifmo.app.lib.Utils.print;

public class InfoCommand implements Command {
    @Override
    public void execute(CommandContext context) throws IOException {
        print(context.writer(), (
            "Creation Date: " + context.vehicles().creationDate() + "\n" +
            "Collection Type: " + context.vehicles().collectionType() + "\n" +
            "Collection Size: " + context.vehicles().stream().count() + "\n"
        ));
    }

    @Override
    public String helpMessage() {
        return "Print out the info about current collection";
    }
}
