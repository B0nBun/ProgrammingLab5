package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;

public class GroupCountingByIdCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws IOException {
        var grouped = vehicles
            .stream()
            .map(vehicle -> {
                var withSameId = vehicles
                    .stream()
                    .filter(v -> v.id() == vehicle.id())
                    .count();
                return new SimpleEntry<>(vehicle.id(), withSameId);
            });

        var joined = grouped
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n"));
        
        if (joined.equals("")) {
            Utils.print(writer, "The collection is empty");
            return;
        }
        Utils.print(writer, joined + "\n");
    }

    @Override
    public String helpMessage() {
        return "Group elements by the id field and print the number of them in every group";
    }
}
