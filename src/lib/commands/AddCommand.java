package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Vehicles;
import lib.Vehicles.VehicleCreationSchema;
import lib.exceptions.CommandNotFoundException;
import lib.exceptions.InvalidArgumentException;

public class AddCommand implements Command {
    @Override
    public void execute(
        String[] _arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> _commandsMap
    ) throws CommandNotFoundException, InvalidArgumentException, IOException {
        VehicleCreationSchema creationSchema = VehicleCreationSchema.createFromScanner(scanner, writer);
        vehicles.add(creationSchema);
    }

    @Override
    public String helpMessage() {
        return "Add a new vehicle to the collection";
    }
}
