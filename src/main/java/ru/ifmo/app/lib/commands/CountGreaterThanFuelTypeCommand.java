package ru.ifmo.app.lib.commands;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.ParsingException;

public class CountGreaterThanFuelTypeCommand implements Command {
    @Override
    public void execute(CommandContext context) throws InvalidArgumentException, InvalidNumberOfArgumentsException {
        if (context.arguments().length < 1)
            throw new InvalidNumberOfArgumentsException(1, context.arguments().length);
        
        try {
            FuelType chosenType = FuelType.parse(context.arguments()[0]);
            
            var count = context.vehicles().stream()
                .filter(v -> v.fuelType().compareTo(chosenType) > 0)
                .count();
            App.logger.info("Number of vehicles with greater fuel type: {}", count);
        } catch (ParsingException err) {
            throw new InvalidArgumentException("type", err.getMessage());
        }
    }

    @Override
    public String[] helpArguments() {
        return new String[] {"fuelType"};
    }
    
    @Override
    public String helpMessage() {
        return "Prints out the number of elements, fuel type of which is greater than a provided one";
    }
}
