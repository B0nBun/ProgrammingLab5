package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.entities.Vehicle;

public class AddIfMaxCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        var creationSchema = VehicleCreationSchema.createFromScanner(scanner, writer);   
        var maxVehicle = vehicles.stream().max(Vehicle::compareTo).orElse(null);
        
        if (maxVehicle == null || creationSchema.generate(vehicles.peekNextId(), vehicles.peekNextCreationDate()).compareTo(maxVehicle) > 0) {
            vehicles.add(creationSchema);
        }
    }

    @Override
    public String helpMessage() {
        return "Add a new element to the collection if it's greater than the current max element";
    }
}
