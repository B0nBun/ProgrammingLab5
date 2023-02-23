package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;

public class AddCommand implements Command {
    @Override
    public void execute(CommandContext context) throws IOException {
        VehicleCreationSchema creationSchema = VehicleCreationSchema.createFromScanner(context.scanner(), context.writer());
        context.vehicles().add(creationSchema);
    }

    @Override
    public String helpMessage() {
        return "Add a new vehicle to the collection";
    }
}
