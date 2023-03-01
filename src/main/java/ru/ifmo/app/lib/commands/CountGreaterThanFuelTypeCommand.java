package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to log out the count of vehicles, fuel type of which is greater than the one passed
 * in the arguments.
 */
public class CountGreaterThanFuelTypeCommand implements Command {
  @Override
  public void execute(CommandContext context)
      throws InvalidArgumentException, InvalidNumberOfArgumentsException {
    if (context.arguments().length < 1)
      throw new InvalidNumberOfArgumentsException(1, context.arguments().length);

    try {
      FuelType chosenType = FuelType.parse(context.arguments()[0]);

      var count =
          context.vehicles().stream().filter(v -> v.fuelType().compareTo(chosenType) > 0).count();

      App.logger.info(Messages.get("NumberOfVehiclesWithGreaterFuelType", count));
    } catch (ParsingException err) {
      throw new InvalidArgumentException(Messages.get("Vehicle.VehicleType"), err.getMessage());
    }
  }

  @Override
  public String[] helpArguments() {
    return new String[] {Messages.get("Help.Command.Arg.FuelType")};
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.CountGreaterThanFuelType");
  }
}
