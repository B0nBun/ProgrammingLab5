package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

public class RemoveByIdCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);
        
        try {
            Long vehicleId = Long.parseUnsignedLong(arguments[0]);

            var found = vehicles.removeIf(v -> v.id() == vehicleId);

            if (!found) {
                Utils.print(writer, "Vehicle with id " + vehicleId + " not found\n");
            }
        } catch (NumberFormatException err) {
            throw new InvalidArgumentException("id", "id must be an unsigned long integer");
        }
    }

    @Override
    public String[] helpArguments() {
        return new String[] {"id"};
    }

    @Override
    public String helpMessage() {
        return "Remove element with specified 'id'";
    }
}
