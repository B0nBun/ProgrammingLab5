package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.entities.Vehicle;

public class AddIfMaxCommand implements Command {
    @Override
    public void execute(CommandContext context) throws IOException {
        var creationSchema = VehicleCreationSchema.createFromScanner(context.scanner(), context.writer());   
        var vehicles = context.vehicles();
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
