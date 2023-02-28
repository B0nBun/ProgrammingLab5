package ru.ifmo.app.lib.commands;

import java.util.UUID;
import java.util.function.BiPredicate;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.utils.Messages;

public class UpdateCommand implements Command {
    @Override
    public void execute(CommandContext context) throws InvalidNumberOfArgumentsException {
        if (context.arguments().length < 1)
            throw new InvalidNumberOfArgumentsException(1, context.arguments().length);
        
        String vehicleUUID = context.arguments()[0];

        BiPredicate<UUID, String> compareUUIDs = (uuid, inputString) -> {
            return uuid.toString().startsWith(inputString);
        };
        
        Vehicle found = context.vehicles().stream()
            .filter(v -> compareUUIDs.test(v.id(), vehicleUUID))
            .findFirst()
            .orElseGet(() -> null);
        
        if (found == null) {
            App.logger.warn(Messages.get("Warn.VehicleStartingWithIdNotFound", vehicleUUID));
            return;
        }
        App.logger.info(Messages.get("UpdatingVehicleWithId", found.id()));

        var updatedSchema = VehicleCreationSchema.createFromScanner(context.scanner(), context.writer(), new VehicleCreationSchema(found));

        context.vehicles().mutate(vehicle -> {
            if (compareUUIDs.test(vehicle.id(), vehicleUUID)) {
                return updatedSchema.generate(vehicle.id(), vehicle.creationDate());
            }
            return vehicle;
        });
    }

    @Override
    public String[] helpArguments() {
        return new String[] {
            Messages.get("Help.Command.Arg.Id")
        };
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Update");
    }
}
