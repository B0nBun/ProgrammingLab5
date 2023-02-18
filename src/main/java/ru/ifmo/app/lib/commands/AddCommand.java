package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;

public class AddCommand implements Command {
    @Override
    public void execute(
        String[] _arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> _commandsMap
    ) throws IOException {
        VehicleCreationSchema creationSchema = VehicleCreationSchema.createFromScanner(scanner, writer);
        vehicles.add(creationSchema);
    }

    @Override
    public String helpMessage() {
        return "Add a new vehicle to the collection";
    }
}
