package ru.ifmo.app.local.lib.commands;

import java.util.UUID;
import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.local.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.shared.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.utils.Messages;

/**
 * Command used to update the vehicle in the collection. Accepts as the arguments a string, which is
 * then interpreted as a start of the chose vehicle's uuid. If such vehicle is found, the user is
 * asked to fill each field as a prompt. Otherwise a warning is logged and command ends.
 */
public class UpdateCommand implements DeprecatedCommand {

    private static boolean uuidStartsWith(UUID uuid, String string) {
        return uuid.toString().startsWith(string);
    }

    @Override
    public void execute(DeprecatedCommandContext context)
        throws InvalidNumberOfArgumentsException {
        if (context.arguments().length < 1) throw new InvalidNumberOfArgumentsException(
            1,
            context.arguments().length
        );

        String vehicleUUID = context.arguments()[0];

        Vehicle found = context
            .vehicles()
            .stream()
            .filter(v -> uuidStartsWith(v.id(), vehicleUUID))
            .findFirst()
            .orElseGet(() -> null);

        if (found == null) {
            App.logger.warn(
                Messages.get("Warn.VehicleStartingWithIdNotFound", vehicleUUID)
            );
            return;
        }
        App.logger.info(Messages.get("UpdatingVehicleWithId", found.id()));

        var updatedSchema = VehicleCreationSchema.createFromScanner(
            context.scanner(),
            new VehicleCreationSchema(found),
            context.executedByScript()
        );

        context
            .vehicles()
            .mutate(vehicle -> {
                if (uuidStartsWith(vehicle.id(), vehicleUUID)) {
                    return updatedSchema.generate(vehicle.id(), vehicle.creationDate());
                }
                return vehicle;
            });
    }

    @Override
    public String[] helpArguments() {
        return new String[] { Messages.get("Help.Command.Arg.Id") };
    }

    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Update");
    }
}
