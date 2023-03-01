package ru.ifmo.app.lib.commands;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to create a vehicle (the same way the {@link AddCommand} works) and
 * add it to the collection only if it's greater than all of the elements.
 */
public class AddIfMaxCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        var creationSchema = VehicleCreationSchema.createFromScanner(context.scanner());   
        var vehicles = context.vehicles();
        var maxVehicle = vehicles.stream().max(Vehicle::compareTo).orElse(null);
        
        if (maxVehicle == null || creationSchema.generate(vehicles.peekNextId(), vehicles.peekNextCreationDate()).compareTo(maxVehicle) > 0) {
            vehicles.add(creationSchema);
        }
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.AddIfMax");
    }
}
