package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Vehicles;
import lib.Vehicles.VehicleCreationSchema;
import lib.entities.Vehicle;

public class AddIfMinCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        var creationSchema = VehicleCreationSchema.createFromScanner(scanner, writer);   
        var minVehicle = vehicles.stream().min(Vehicle::compareTo).orElse(null);
        
        if (minVehicle == null || creationSchema.generate(vehicles.nextId(), vehicles.nextCreationDate()).compareTo(minVehicle) < 0) {
            vehicles.add(creationSchema);
        }
    }

    @Override
    public String helpMessage() {
        return "Add a new element to the collection if it's less than the current minimal element";
    }
}
