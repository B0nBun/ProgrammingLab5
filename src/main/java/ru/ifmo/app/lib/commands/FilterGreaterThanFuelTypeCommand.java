package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.stream.Collectors;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.ParsingException;

public class FilterGreaterThanFuelTypeCommand implements Command {
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
            
            var filtered = vehicles.stream()
                .filter(v -> v.fuelType().compareTo(chosenType) > 0)
                .map(v -> v.toString())
                .collect(Collectors.joining("\n"))
                .trim();
                
            if (filtered.equals("")) {
                Utils.print(writer, "There are no elements with greater fuel type\n");
                return;
            }
            Utils.print(writer, filtered + "\n");
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
        return "Prints out the elements, fuel type of which is greater than a provided one";
    }
}
