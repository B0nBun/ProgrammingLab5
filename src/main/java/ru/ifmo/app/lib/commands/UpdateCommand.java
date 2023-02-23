package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.BiPredicate;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

public class UpdateCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws IOException, InvalidNumberOfArgumentsException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);
        
        String vehicleUUID = arguments[0];

        BiPredicate<UUID, String> compareUUIDs = (uuid, inputString) -> {
            return uuid.toString().startsWith(inputString);
        };
        
        Vehicle found = vehicles.stream()
            .filter(v -> compareUUIDs.test(v.id(), vehicleUUID))
            .findFirst()
            .orElseGet(() -> null);
        
        // TODO: Поменять это сообщение на Vehicle with id startingWith {vehicleUUID} not found
        if (found == null) {
            Utils.print(writer, "Vehicle with id " + vehicleUUID + " not found\n");
            return;
        }

        var updatedSchema = VehicleCreationSchema.createFromScanner(scanner, writer, new VehicleCreationSchema(found));

        // TODO: Вывести uuid найденного vehicle
        vehicles.mutate(vehicle -> {
            if (compareUUIDs.test(vehicle.id(), vehicleUUID)) {
                return updatedSchema.generate(vehicle.id(), vehicle.creationDate());
            }
            return vehicle;
        });
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
