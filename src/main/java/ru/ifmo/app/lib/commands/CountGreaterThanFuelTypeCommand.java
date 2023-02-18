package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.entities.VehicleType;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
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
        Map<String, Command> commandsMap
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException, ExitProgramException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);
        
        try {
            VehicleType chosenType = VehicleType.parse(arguments[0]);
            
            var count = vehicles.stream()
                .filter(v -> v.type().compareTo(chosenType) > 0)
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
