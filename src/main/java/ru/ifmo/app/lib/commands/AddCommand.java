package ru.ifmo.app.lib.commands;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.utils.Messages;

public class AddCommand implements Command {
    @Override
    public void execute(CommandContext context) {
        VehicleCreationSchema creationSchema = VehicleCreationSchema.createFromScanner(context.scanner());
        context.vehicles().add(creationSchema);
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Add");
    }
}
