package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.ParsingException;

public class CountGreaterThanFuelTypeCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);
        
        try {
            FuelType chosenType = FuelType.parse(arguments[0]);
            
            var count = vehicles.stream()
                .filter(v -> v.fuelType().compareTo(chosenType) > 0)
                .count();
            Utils.print(writer, "Number of vehicles with greater fuel type: " + count + "\n");
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
