package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;

public class GroupCountingByIdCommand implements Command {
    @Override
    public void execute(CommandContext context) throws IOException {
        var grouped = context.vehicles()
            .stream()
            .map(vehicle -> {
                var withSameId = context.vehicles()
                    .stream()
                    .filter(v -> v.id() == vehicle.id())
                    .count();
                return new SimpleEntry<>(vehicle.id(), withSameId);
            });

        var joined = grouped
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n"));
        
        if (joined.equals("")) {
            Utils.print(context.writer(), "The collection is empty");
            return;
        }
        Utils.print(context.writer(), joined + "\n");
    }

    @Override
    public String helpMessage() {
        return "Group elements by the id field and print the number of them in every group";
    }
}
