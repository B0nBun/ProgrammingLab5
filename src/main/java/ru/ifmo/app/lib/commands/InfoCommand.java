package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import static ru.ifmo.app.lib.Utils.print;
import ru.ifmo.app.lib.Vehicles;

public class InfoCommand implements Command {
    @Override
    public void execute(
        String[] _arguments,
        Vehicles vehicles,
        Scanner _scanner,
        Writer writer,
        Map<String, Command> _commandsMap
    ) throws IOException {
        print(writer, (
            "Creation Date: " + vehicles.creationDate() + "\n" +
            "Collection Type: " + vehicles.collectionType() + "\n" +
            "Collection Size: " + vehicles.stream().count() + "\n"
        ));
    }

    @Override
    public String helpMessage() {
        return "Print out the info about current collection";
    }
}