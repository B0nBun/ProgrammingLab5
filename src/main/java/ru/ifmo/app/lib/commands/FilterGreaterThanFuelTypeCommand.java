package ru.ifmo.app.lib.commands;

import java.util.stream.Collectors;
import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to log out all of the elements, fuel type of which is greater than the one passed in
 * the arguments
 */
public class FilterGreaterThanFuelTypeCommand implements Command {
  @Override
  public void execute(CommandContext context)
      throws InvalidArgumentException, InvalidNumberOfArgumentsException {
    if (context.arguments().length < 1)
      throw new InvalidNumberOfArgumentsException(1, context.arguments().length);

    try {
      FuelType chosenType = FuelType.parse(context.arguments()[0]);

      var filtered = context.vehicles().stream().filter(v -> v.fuelType().compareTo(chosenType) > 0)
          .map(v -> v.toString()).collect(Collectors.joining("\n")).trim();

      if (filtered.equals("")) {
        App.logger.info(Messages.get("NoElementsWithGreaterFuelType"));
        return;
      }
      App.logger.info(filtered);
    } catch (ParsingException err) {
      throw new InvalidArgumentException("type", err.getMessage());
    }
  }

  @Override
  public String[] helpArguments() {
    return new String[] {Messages.get("Help.Command.Arg.FuelType")};
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.FilterGreaterThanFuelType");
  }
}
