package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.entities.VehicleType;
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
        Map<String, Command> commandsMap
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);
        
        try {
            VehicleType chosenType = VehicleType.parse(arguments[0]);
            
            var filtered = vehicles.stream()
                .filter(v -> v.type().compareTo(chosenType) > 0)
                .map(v -> v.toString())
                .collect(Collectors.joining("\n"));
                
            if (filtered == "") {
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
