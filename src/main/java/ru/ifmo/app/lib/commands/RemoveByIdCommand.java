package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

public class RemoveByIdCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws InvalidNumberOfArgumentsException, IOException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);
        
        String vehicleUUID = arguments[0];

        // TODO: Выводить все удаленные uuid
        var found = vehicles.removeIf(v -> v.id().toString().startsWith(vehicleUUID));

        // TODO: Поменять это сообщение на Vehicle with id startingWith {vehicleUUID} not found
        if (!found) {
            Utils.print(writer, "Vehicle with id " + vehicleUUID + " not found\n");
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
