package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.DeprecatedCommand;
import ru.ifmo.app.lib.DeprecatedCommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to remove a vehicle which is lower than the inputted one. User asked to create a new
 * vehicle (not add) and then, using {@link ru.ifmo.app.lib.entities.Vehicle#compareTo
 * Vehicle#compareTo} method, every vehicle in the collection, that has lower value than the created
 * one is removed.
 */
public class RemoveLowerCommand implements DeprecatedCommand {
  @Override
  public void execute(DeprecatedCommandContext context) {
    var creationSchema =
        VehicleCreationSchema.createFromScanner(context.scanner(), context.executedByScript());
    var notAddedVehicle = creationSchema.generate(context.vehicles().peekNextId(),
        context.vehicles().peekNextCreationDate());
    context.vehicles().removeIf(vehicle -> {
      if (vehicle.compareTo(notAddedVehicle) < 0) {
        App.logger.info(Messages.get("RemovingVehicleWithId", vehicle.id()));
        return true;
      }
      return false;
    });
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.RemoveLowerCommand");
  }
}
