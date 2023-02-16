package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Utils;
import lib.Vehicles;
import lib.Vehicles.VehicleCreationSchema;
import lib.entities.Vehicle;
import lib.exceptions.InvalidArgumentException;
import lib.exceptions.InvalidNumberOfArgumentsException;

public class UpdateCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException, InvalidArgumentException, InvalidNumberOfArgumentsException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);
        
        try {
            Long vehicleId = Long.parseUnsignedLong(arguments[0]);

            Vehicle found = vehicles.stream()
                .filter(v -> v.id() == vehicleId)
                .findFirst()
                .orElseGet(() -> null);
            
            if (found == null) {
                Utils.print(writer, "Vehicle with id " + vehicleId + " not found\n");
                return;
            }

            var updatedSchema = VehicleCreationSchema.createFromScanner(scanner, writer, new VehicleCreationSchema(found));

            vehicles.mutate(vehicle -> {
                if (vehicle.id() == vehicleId) {
                    return updatedSchema.generate(vehicle.id(), vehicle.creationDate());
                }
                return vehicle;
            });
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
        return "Update element with specified 'id'";
    }
}
