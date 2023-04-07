package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.DeprecatedCommand;
import ru.ifmo.app.lib.DeprecatedCommandContext;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to remove a vehilce by id. Requiers one argument which is interpreted as a start of
 * vehicle's uuid. If such elements are not found, the messsage is logged and the command finishes.
 * Otherwise all of the vehicles, ids of which start with provided string are removed
 */
public class RemoveByIdCommand implements DeprecatedCommand {
  @Override
  public void execute(DeprecatedCommandContext context) throws InvalidNumberOfArgumentsException {
    if (context.arguments().length < 1)
      throw new InvalidNumberOfArgumentsException(1, context.arguments().length);

    String vehicleUUID = context.arguments()[0];

    var found = context.vehicles().removeIf(v -> {
      if (v.id().toString().startsWith(vehicleUUID)) {
        App.logger.info(Messages.get("RemovingVehicleWithId", v.id()));
        return true;
      }
      return false;
    });

    if (!found) {
      App.logger.warn(Messages.get("Warn.VehicleStartingWithIdNotFound", vehicleUUID));
    }
  }

  @Override
  public String[] helpArguments() {
    return new String[] {Messages.get("Help.Command.Arg.Id")};
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.RemoveById");
  }
}
