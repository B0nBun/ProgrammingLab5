package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.exceptions.RuntimeIOException;

public class RemoveLowerCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws IOException {
        var creationSchema = VehicleCreationSchema.createFromScanner(scanner, writer);
        var notAddedVehicle = creationSchema.generate(vehicles.peekNextId(), vehicles.peekNextCreationDate());
        try {
            vehicles.removeIf(vehicle -> {
                if (vehicle.compareTo(notAddedVehicle) < 0) {
                    try {
                        Utils.print(writer, "Removing vehicle with id = " + vehicle.id() + "\n");
                    } catch (IOException err) {
                        throw new RuntimeIOException(err);
                    }
                    return true;
                }
                return false;
            });
        } catch (RuntimeIOException err) {
            throw err.iocause;
        }
    }

    @Override
    public String helpMessage() {
        return "Remove from the collection all of the elements, which are lower than a specified one";
    }
}
