package ru.ifmo.app.lib.commands;

import ru.ifmo.app.lib.DeprecatedCommand;
import ru.ifmo.app.lib.DeprecatedCommandContext;
import ru.ifmo.app.lib.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to add an element to the collection.
 *
 * <p>
 * User is prompted to input every field in the VehicleCreationSchema with
 * {@link VehicleCreationSchema#createFromScanner} method, after which, the
 * {@link ru.ifmo.app.lib.entities.Vehicle Vehicle} generated from this schema is added to the
 * collection.
 */
public class AddCommand implements DeprecatedCommand {
  @Override
  public void execute(DeprecatedCommandContext context) {
    VehicleCreationSchema creationSchema =
        VehicleCreationSchema.createFromScanner(context.scanner(), context.executedByScript());
    context.vehicles().add(creationSchema);
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Add");
  }
}
