package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;

public class ClearCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws IOException {
        vehicles.clear();        
        Utils.print(writer, "Collection cleared!\n");
    }

    @Override
    public String helpMessage() {
        return "Clears the collection";
    }
}
